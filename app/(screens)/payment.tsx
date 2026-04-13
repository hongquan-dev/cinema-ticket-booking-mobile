import useNotification from '@/src/hooks/useNotification';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, Image, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import bookingApi from '../../src/api/bookingApi';
import useConfirm from '../../src/hooks/useConfirm';

const PaymentOption = ({
    id,
    title,
    image,
    selected,
    onSelect
}: {
    id: string,
    title: string,
    image: any,
    selected: boolean,
    onSelect: (id: string) => void
}) => (
    <TouchableOpacity
        onPress={() => onSelect(id)}
        className={`flex-row items-center p-4 mb-3 rounded-xl border ${selected ? 'border-pink-500 bg-[#2d325a]' : 'border-gray-600 bg-transparent'}`}
    >
        <View className={`w-6 h-6 rounded-full border-2 items-center justify-center ${selected ? 'border-pink-500' : 'border-gray-500'}`}>
            {selected && <View className="w-3 h-3 rounded-full bg-pink-500" />}
        </View>
        <Image source={image} className="ml-4" style={{ width: 60, height: 16 }} />
        <Text className="text-white text-xl ml-3 font-medium">{title}</Text>
    </TouchableOpacity>
);

const Payment = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const [selectedMethod, setSelectedMethod] = useState('vietqr');
    const [isAgreed, setIsAgreed] = useState(false);
    const [isBooking, setIsBooking] = useState(false);
    const [userId, setUserId] = useState<string | null>(null);
    const { confirm, ConfirmComponent } = useConfirm();
    const { notify, NotificationComponent } = useNotification();

    const showtime: any = params.showtime ? JSON.parse(params.showtime as string) : null;
    const seatIds: string[] = params.seatIds ? (params.seatIds as string).split(',') : [];
    const seatNames: string = (params.seatNames as string) ?? '';
    const totalPrice: string = (params.totalPrice as string) ?? '0';

    const movieData = showtime?.movie;
    const showtimeId = showtime?.id;

    const paymentMethods = [
        { id: 'vietqr', title: 'VietQR', logo: require('../../assets/payment_options/vietqr_logo.png') },
        { id: 'vnpay', title: 'VNPay', logo: require('../../assets/payment_options/vnpay_logo.webp') },
        { id: 'viettel', title: 'Viettel Money', logo: require('../../assets/payment_options/viettelmoney_logo.png') },
        { id: 'momo', title: 'MoMo', logo: require('../../assets/payment_options/momo_logo.jpg') },
    ];

    const formatShowtime = (isoString: string) => {
        if (!isoString) return "";

        const date = new Date(isoString);

        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');

        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();

        return `${hours}:${minutes} - ${day}/${month}/${year}`;
    };

    useFocusEffect(
        useCallback(() => {
            const getToken = async () => {
                const rawUserId = await AsyncStorage.getItem('user_id');
                const cleanUserId = rawUserId ? rawUserId.replace(/"/g, '') : null;
                setUserId(cleanUserId);
            }
            getToken();
        }, [])
    );

    const handlePayment = async () => {
        if (!userId) {
            return;
        }
        const isConfirmed = await confirm("Thanh toán", `Bạn có chắc chắn muốn thanh toán ${totalPrice} VND?`, "info");
        if (!isConfirmed) return;

        setIsBooking(true);

        try {
            const response = await bookingApi.bookTickets({
                showtimeId: showtimeId,
                seatIds: seatIds,
                userId: userId,
            });
            await notify("Thành công", "Đặt vé thành công", "success");
            router.replace('/(tabs)/tickets');
        } catch (error) {
            console.error("Error creating booking:", error);
            notify("Thất bại", "Đặt vé thất bại", "error");
        } finally {
            setIsBooking(false);
        }
    }

    if (!showtime || !seatIds || !seatNames || !totalPrice) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />
            {ConfirmComponent}
            {NotificationComponent}

            {/* HEADER */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Thanh Toán</Text>
                <View className="w-[28px]" />
            </View>

            <ScrollView className="flex-1 px-5" showsVerticalScrollIndicator={false}>
                {/* MOVIE INFO SECTION */}
                <View className="flex-row mt-1 mb-4">
                    <Image
                        source={{ uri: movieData.posterUrl }}
                        className="w-40 h-64 rounded-lg"
                        resizeMode="cover"
                    />
                    <View className="flex-1 ml-4 pt-1">
                        <Text className="text-white text-xl font-bold leading-6" numberOfLines={2}>
                            {movieData.movieName}
                        </Text>
                        <View className="flex-row mt-2">
                            <View className="border border-pink-500 px-2 py-0.5 rounded mr-2">
                                <Text className="text-pink-500 text-lg font-bold">{showtime.format}</Text>
                            </View>
                        </View>
                        <View className="mt-5 gap-y-1">
                            <Text className="text-gray-300 text-xl">• {formatShowtime(showtime?.startTime)}</Text>
                            <Text className="text-gray-300 text-xl">• {showtime?.room?.name}</Text>
                            <Text className="text-gray-300 text-xl">• Ghế: {seatNames}</Text>
                            <Text className="text-gray-300 text-xl">• Tổng cộng: {Number(totalPrice).toLocaleString()} VND</Text>
                        </View>
                    </View>
                </View>

                <View className="h-[1px] bg-gray-700 w-full mb-5" />

                {/* PAYMENT METHODS SECTION */}
                <Text className="text-white text-xl font-bold mb-4">Hình thức thanh toán</Text>

                {paymentMethods.map((item) => (
                    <PaymentOption
                        key={item.id}
                        id={item.id}
                        title={item.title}
                        image={item.logo}
                        selected={selectedMethod === item.id}
                        onSelect={setSelectedMethod}
                    />
                ))}

                {/* AGREEMENT CHECKBOX */}
                <TouchableOpacity
                    onPress={() => setIsAgreed(!isAgreed)}
                    className="flex-row items-center mt-1 mb-6"
                >
                    <View className={`w-6 h-6 rounded-full border-2 items-center justify-center ${isAgreed ? 'border-white bg-transparent' : 'border-white'}`}>
                        {isAgreed && <View className="w-3 h-3 rounded-full bg-white" />}
                    </View>
                    <Text className="text-white text-md ml-3 flex-1">
                        Tôi xác nhận các thông tin đã chính xác và đồng ý với các
                        <Text className="text-pink-500"> và điều khoản & chính sách</Text>
                    </Text>
                </TouchableOpacity>

                {/* FOOTER BUTTON */}
                <View className="px-4 pb-3 pt-2">
                    <TouchableOpacity
                        disabled={!isAgreed || isBooking}
                        className={`w-full py-4 rounded-xl items-center ${isAgreed ? 'bg-gray-300' : 'bg-gray-500'}`}
                        onPress={handlePayment}
                    >
                        <Text className={`text-lg font-bold ${isAgreed ? 'text-[#272b50]' : 'text-gray-300'}`}>
                            TIẾN HÀNH THANH TOÁN
                        </Text>
                    </TouchableOpacity>
                </View>

                <View className="mt-6 px-1">
                    <Text className="text-white text-[16px] font-medium mb-2">
                        Lưu ý trước khi thanh toán:
                    </Text>
                    <Text className="text-gray-300 text-[15px] leading-6">
                        - Khách thanh toán xong quay lại app để đợi nhận vé
                    </Text>
                    <View className="flex-row flex-wrap">
                        <Text className="text-gray-300 text-[15px] leading-6">
                            - Nếu không nhận được vé khách hàng liên hệ số
                        </Text>
                        <Text className="text-white text-[15px] font-bold leading-6"> 0865.205.608 </Text>
                        <Text className="text-gray-300 text-[15px] leading-6">
                            Để được hỗ trợ
                        </Text>
                    </View>
                </View>
            </ScrollView>
        </View>
    );
};

export default Payment;