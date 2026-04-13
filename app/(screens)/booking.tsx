import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import React, { useCallback, useMemo, useState } from 'react';
import { ActivityIndicator, Dimensions, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import { CustomerType, SeatType } from '../../src/enums/ticketPriceEnums';
import useConfirm from '../../src/hooks/useConfirm';
import bookingApi from '../../src/services/bookingApi';
import showtimeApi from '../../src/services/showtimeApi';
import ticketPriceApi from '../../src/services/ticketPriceApi';

const { width } = Dimensions.get('window');

const SEAT_MARGIN = 2;
const SIDE_PADDING = 40;

const BookingPage = () => {
    const { confirm, ConfirmComponent } = useConfirm();
    const router = useRouter();
    const params = useLocalSearchParams();
    const showtimeId = params.showtimeId;

    const [seats, setSeats] = useState<any[]>([]);
    const [selectedSeats, setSelectedSeats] = useState<any[]>([]);
    const [showtime, setShowtime] = useState<any>(null);
    const [userId, setUserId] = useState<string | null>(null);
    // Default ticket prices if call api not success
    const [ticketPrices, setTicketPrices] = useState([
        {
            seatType: SeatType.STANDARD.value,
            ticketPrice: 60000,
            label: SeatType.STANDARD.label
        },
        {
            seatType: SeatType.VIP.value,
            ticketPrice: 90000,
            label: SeatType.VIP.label
        }
    ]);

    const getDayInfo = (isoString: string) => {
        if (!isoString) return { date: "", dayType: "WEEKDAY" };

        const datePart = isoString.split('T')[0];
        const date = new Date(datePart);
        const day = date.getDay();
        const dayType = (day === 0 || day === 5 || day === 6) ? "WEEKEND" : "WEEKDAY";

        return { date: datePart, dayType: dayType };
    };

    const getTime = (isoString: string) => {
        if (!isoString) return "00:00:00";

        const timePart = isoString.split('T')[1];

        return timePart.substring(0, 8);
    };

    const getRoomType = (roomType: string) => {
        if (roomType === "2D") return "_2D";
        if (roomType === "3D") return "_3D";
        return roomType;
    };

    const toggleSeat = (seat: any) => {
        setSelectedSeats(prev =>
            prev.find(s => s.id === seat.id)
                ? prev.filter(s => s.id !== seat.id)
                : [...prev, seat]
        );
    };

    useFocusEffect(
        useCallback(() => {
            const loadInitialData = async () => {
                if (!showtimeId) return;

                try {
                    const [seatRes, stRes]: any = await Promise.all([
                        bookingApi.getSeatLayout(showtimeId as string),
                        showtimeApi.getById(showtimeId as string)
                    ]);

                    if (seatRes?.seats) {
                        setSeats(seatRes.seats);
                    }

                    if (stRes?.data) {
                        setShowtime(stRes);
                        await fetchTicketPrices(stRes.data);
                    }
                } catch (error) {
                    // console.error("Error loading data:", error);
                }
            };

            loadInitialData();
        }, [showtimeId])
    );

    const fetchTicketPrices = async (stData: any) => {
        const dayInfo = getDayInfo(stData.startTime);
        const time = getTime(stData.startTime);
        const format = getRoomType(stData.room.roomType);
        try {
            const [stdPrice, vipPrice]: any = await Promise.all([
                ticketPriceApi.calculatePrice({
                    movieFormat: format,
                    seatType: SeatType.STANDARD.value,
                    dayType: dayInfo.dayType,
                    customerType: CustomerType.ADULT.value,
                    startTime: time
                }),
                ticketPriceApi.calculatePrice({
                    movieFormat: format,
                    seatType: SeatType.VIP.value,
                    dayType: dayInfo.dayType,
                    customerType: CustomerType.ADULT.value,
                    startTime: time
                })
            ]);

            if (stdPrice && vipPrice) {
                setTicketPrices([
                    {
                        seatType: SeatType.STANDARD.value,
                        ticketPrice: stdPrice.ticketPrice,
                        label: SeatType.STANDARD.label
                    },
                    {
                        seatType: SeatType.VIP.value,
                        ticketPrice: vipPrice.ticketPrice,
                        label: SeatType.VIP.label
                    }
                ]);
            }
        } catch (error) {
            // console.error("Error fetching ticket prices:", error);
        }
    };

    // Calculate total price based on selected seats and their types
    const totalPrice = useMemo(() => {
        return selectedSeats.reduce((sum, seat) => {
            // Find the price for the specific seat type from the API data
            const priceInfo = ticketPrices.find(p => p.seatType === seat.seatType);
            return sum + (priceInfo ? priceInfo.ticketPrice : 0);
        }, 0);
    }, [selectedSeats, ticketPrices]);

    // Create a list of seat IDs or numbers for the API booking request
    const selectedSeatDetails = useMemo(() => {
        return {
            ids: selectedSeats.map(s => s.id),
            names: selectedSeats.map(s => s.seatNumber).join(', ')
        };
    }, [selectedSeats]);

    const groupedSeats = useMemo(() => {
        const groups: { [key: number]: any[] } = {};
        seats.forEach(seat => {
            if (!groups[seat.rowIndex]) groups[seat.rowIndex] = [];
            groups[seat.rowIndex].push(seat);
        });
        Object.keys(groups).forEach(key => {
            groups[Number(key)].sort((a, b) => a.colIndex - b.colIndex);
        });
        return groups;
    }, [seats]);

    const maxCols = useMemo(() => {
        return Math.max(...seats.map(s => s.colIndex), 1);
    }, [seats]);

    const SEAT_SIZE = Math.floor((width - SIDE_PADDING) / (maxCols + 2));

    useFocusEffect(
        useCallback(() => {
            const getToken = async () => {
                const userId = await AsyncStorage.getItem('user_id');
                setUserId(userId);
            }
            getToken();
        }, [])
    );

    const handlePayment = async () => {
        if (!userId) {
            const isConfirm = await confirm("Thông báo", "Bạn cần đăng nhập để thực hiện chức năng này", "info");
            if (!isConfirm) return;
            router.push('/login');
            return;
        }
        router.push({
            pathname: '/payment',
            params: {
                showtime: JSON.stringify(showtime.data),
                seatIds: selectedSeatDetails.ids.join(','),
                seatNames: selectedSeatDetails.names,
                totalPrice: totalPrice.toString(),
            },
        });
    }

    if (seats.length === 0 || !showtime) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#1a1d3d]">
            <StatusBar barStyle="light-content" />
            {ConfirmComponent}

            {/* Custom Header */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Chọn ghế</Text>
                <View className="w-[28px]" />
            </View>

            {/* Screen */}
            <View className="items-center mt-5 mb-10">
                <View className="w-[80%] h-[4px] bg-white rounded-full shadow-lg shadow-white" />
                <View
                    style={{
                        borderTopWidth: 20,
                        borderTopColor: 'rgba(255,255,255,0.1)',
                        borderLeftWidth: 50,
                        borderLeftColor: 'transparent',
                        borderRightWidth: 50,
                        borderRightColor: 'transparent',
                        width: width * 0.8,
                        marginTop: 5
                    }}
                />
            </View>

            <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
                {/* Seat Map */}
                <View className="items-center">
                    {/* Column numbers */}
                    <View className="flex-row items-center mb-2">
                        <View style={{ width: 24 }} />

                        {Array.from({ length: maxCols }, (_, i) => i + 1).map((colNum) => (
                            <View
                                key={colNum}
                                style={{ width: SEAT_SIZE, marginHorizontal: SEAT_MARGIN }}
                                className="items-center"
                            >
                                <Text className="text-gray-400 text-[11px] font-bold">
                                    {colNum}
                                </Text>
                            </View>
                        ))}

                        <View style={{ width: 24 }} />
                    </View>

                    {Object.keys(groupedSeats).sort((a, b) => Number(a) - Number(b)).map((rowIndex) => {
                        const rowData = groupedSeats[Number(rowIndex)];
                        const rowLabel = rowData[0]?.seatNumber.replace(/[0-9]/g, '');

                        return (
                            <View key={rowIndex} className="flex-row items-center justify-center mb-1">
                                <Text className="text-gray-400 w-6 text-center text-[11px] font-bold">{rowLabel}</Text>

                                <View className="flex-row">
                                    {rowData.map((seat) => {
                                        const isSelected = selectedSeats.some(s => s.id === seat.id);
                                        const isReserved = seat.reserved;

                                        return (
                                            <TouchableOpacity
                                                key={seat.id}
                                                disabled={isReserved}
                                                onPress={() => toggleSeat(seat)}
                                                style={{
                                                    width: SEAT_SIZE,
                                                    height: SEAT_SIZE,
                                                    marginHorizontal: SEAT_MARGIN,
                                                    backgroundColor: isReserved ? '#ef4444' :
                                                        isSelected ? '#ffffff' : seat.colorCode
                                                }}
                                                className={`rounded-[2px] items-center justify-center ${isSelected ? 'border border-blue-500' : ''}`}
                                            >
                                                {isReserved && <Ionicons name="close" size={SEAT_SIZE * 0.7} color="white" />}
                                                {isSelected && <Ionicons name="checkmark" size={SEAT_SIZE * 0.7} color="#1e90ff" />}
                                            </TouchableOpacity>
                                        );
                                    })}
                                </View>

                                <Text className="text-gray-400 w-6 text-center text-[11px] font-bold">{rowLabel}</Text>
                            </View>
                        );
                    })}
                </View>

                {/* Legend */}
                <View className="flex-row flex-wrap justify-center mt-10 px-8">
                    <LegendItem icon={<View className="w-6 h-6 bg-red-500 rounded-sm items-center justify-center"><Ionicons name="close" size={18} color="white" /></View>} label="Đã đặt" />
                    <LegendItem icon={<View className="w-6 h-6 bg-white border border-blue-500 rounded-sm items-center justify-center"><Ionicons name="checkmark" size={18} color="#1e90ff" /></View>} label="Đang chọn" />
                    <View className="flex-row w-full mt-4 justify-center">
                        <LegendItem color="bg-white" label="Ghế thường" />
                        <LegendItem color="bg-orange-500" label="Ghế Vip" />
                    </View>
                </View>

                <View className="h-20" />
            </ScrollView>

            {/* FOOTER */}
            <View className="bg-[#272b50] border-t border-white/10 p-5 pb-10">
                <Text className="text-white text-2xl font-semibold mb-1">
                    {showtime.data.movie.movieName} - T{showtime.data.movie.ageRating}
                </Text>

                <View className="flex-row gap-x-2 mb-3 items-center">
                    <View className="border border-pink-500 px-2 py-0.5 rounded">
                        <Text className="text-pink-500 text-md font-bold">{showtime.data.room.roomType}</Text>
                    </View>
                    <Text className="text-white text-lg font-bold italic">{showtime.data.room.name}</Text>
                </View>

                <View className="flex-row items-center justify-between">
                    <View className="flex-1 mr-4">
                        <Text className="text-gray-400 text-[16px]">
                            {selectedSeats.length > 0 ? `Ghế: ${selectedSeatDetails.names}` : 'Chưa chọn ghế'}
                        </Text>
                        <Text className="text-white text-[20px] font-bold">
                            {selectedSeats.length > 0 ? `${selectedSeats.length} ghế` : '0 ghế'} - {totalPrice.toLocaleString()} VND
                        </Text>
                    </View>

                    <TouchableOpacity
                        className={`px-8 py-3 rounded-xl ${selectedSeats.length > 0 ? 'bg-[#1e90ff]' : 'bg-gray-600'}`}
                        disabled={selectedSeats.length === 0}
                        onPress={() => handlePayment()}
                    >
                        <Text className="text-white font-bold text-xl">ĐẶT VÉ</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </View>
    );
};

const LegendItem = ({ color, label, icon }: { color?: string, label: string, icon?: React.ReactNode }) => (
    <View className="flex-row items-center px-4 py-1">
        {icon ? icon : <View className={`w-6 h-6 ${color} rounded-sm`} />}
        <Text className="text-gray-300 ml-2 text-[14px]">{label}</Text>
    </View>
);

export default BookingPage;