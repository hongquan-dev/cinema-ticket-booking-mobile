import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, FlatList, Image, Modal, Pressable, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import showtimeApi from '../../src/services/showtimeApi';

const SelectShowtime = () => {
    const router = useRouter();
    const params = useLocalSearchParams();

    // State to manage selected date and time slot
    const [days, setDays] = useState<any[]>([]);
    const [timeSlots, setTimeSlots] = useState<any[]>([]);
    const [movieInfo, setMovieInfo] = useState<any>(null);
    const [selectedTimeId, setSelectedTimeId] = useState<string | null>(null);
    const [showtimes, setShowtimes] = useState<any[]>([]);
    const [cinemaId, setCinemaId] = useState<string | null>(null);
    const [selectedDate, setSelectedDate] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    const movieId = params.movieId;
    const [isModalVisible, setIsModalVisible] = useState(false);

    // Array of dates with full details
    useFocusEffect(
        useCallback(() => {
            const loadDates = async () => {
                try {
                    const storedDates = await AsyncStorage.getItem('currentDate');
                    const storedCinemaId = await AsyncStorage.getItem('cinemaId');
                    if (storedCinemaId) {
                        try {
                            setCinemaId(JSON.parse(storedCinemaId));
                        } catch (e) {
                            setCinemaId(storedCinemaId);
                        }
                    }

                    if (storedDates) {
                        const parsedDates = JSON.parse(storedDates);

                        const formattedDays = parsedDates.map((dateStr: string) => {
                            const dateObj = new Date(dateStr);
                            const weekdays = ["Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"];

                            return {
                                fullISO: dateStr,
                                dayName: weekdays[dateObj.getDay()],
                                date: dateObj.getDate().toString().padStart(2, '0'),
                                month: (dateObj.getMonth() + 1).toString().padStart(2, '0'),
                                year: dateObj.getFullYear().toString()
                            };
                        });

                        setDays(formattedDays);
                        if (formattedDays.length > 0) {
                            setSelectedDate(formattedDays[0].fullISO);
                        }
                    }
                } catch (error) {
                    console.error("Error loading dates:", error);
                } finally {
                    setLoading(false);
                }
            };
            loadDates();
        }, [])
    );

    // Fetch showtimes based on selected date and cinema
    useFocusEffect(
        useCallback(() => {
            const fetchShowtimes = async () => {
                if (!selectedDate) return;
                try {
                    const response = await showtimeApi.getGroupedByMovie(movieId as string, {
                        cinemaId: cinemaId,
                        date: selectedDate,
                        roomType: params.roomType
                    });
                    const data = response.data;
                    setShowtimes(response.data.showtimes);
                    setMovieInfo(response.data.movie);

                    if (data.showtimes && data.showtimes.length > 0) {
                        const formattedTimes = data.showtimes.map((st: any) => {
                            const date = new Date(st.startTime);
                            const hours = date.getHours().toString().padStart(2, '0');
                            const minutes = date.getMinutes().toString().padStart(2, '0');
                            return {
                                id: st._id || st.id,
                                time: `${hours}:${minutes}`
                            };
                        });
                        setTimeSlots(formattedTimes);
                    } else {
                        setTimeSlots([]);
                    }
                } catch (error) {
                    console.error("Error fetching showtimes:", error);
                }
            };

            fetchShowtimes();
        }, [selectedDate, cinemaId, movieId, params.roomType])
    );

    if (loading || days.length === 0 || !movieInfo) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#272b50] pb-2">
            <StatusBar barStyle="light-content" />
            <Modal
                animationType="fade"
                transparent={true}
                visible={isModalVisible}
                onRequestClose={() => setIsModalVisible(false)}
            >
                <Pressable
                    className="flex-1 justify-center items-center bg-black/80 px-6"
                    onPress={() => setIsModalVisible(false)}
                >
                    <Pressable
                        className="bg-[#272b50] w-full max-h-[60%] rounded-2xl px-6 py-5 border border-white/10"
                        onPress={(e) => e.stopPropagation()}
                    >
                        <View className="flex-row justify-between items-center mb-2">
                            <Text className="text-white text-xl font-bold">Mô tả phim</Text>
                            <TouchableOpacity onPress={() => setIsModalVisible(false)}>
                                <Ionicons name="close" size={28} color="#ffffff" />
                            </TouchableOpacity>
                        </View>
                        <ScrollView showsVerticalScrollIndicator={true}>
                            <Text className="text-gray-200 text-lg leading-7 text-justify">
                                {movieInfo.description || "Chưa có mô tả cho phim này."}
                            </Text>
                        </ScrollView>
                    </Pressable>
                </Pressable>
            </Modal>

            {/* HEADER SECTION: Navigation and Title */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Chọn suất chiếu</Text>
                <View className="w-[28px]" />
            </View>

            {/* TOP SECTION: Movie Brief Info (Split view) */}
            <View className="flex-row p-5 bg-[#1a1d3d] border-b border-white/10">
                {/* Left: Movie Poster Image */}
                <Image
                    source={{ uri: movieInfo.posterUrl }}
                    className="w-40 h-56 rounded-lg"
                    resizeMode="cover"
                />

                {/* Right: Movie Text Information */}
                <View className="flex-1 ml-5 justify-center">
                    <Text className="text-white text-xl font-bold leading-7 mb-2" numberOfLines={3}>
                        {movieInfo.movieName}
                    </Text>
                    <Text className="text-gray-400 text-lg">Thể loại: {movieInfo.selectedGenres.join(', ')}</Text>
                    <Text className="text-gray-400 text-lg">Thời lượng: {movieInfo.duration} phút</Text>
                    <Text className="text-gray-400 text-lg">Khởi chiếu: {new Date(movieInfo.releaseDate).toLocaleDateString('vi-VN')}</Text>
                    <TouchableOpacity onPress={() => setIsModalVisible(true)}>
                        <Text className="text-lg italic text-lg text-gray-400 underline mt-2 font-light">Click để xem mô tả phim</Text>
                    </TouchableOpacity>
                </View>
            </View>

            {/* BOTTOM SECTION: Selection Controls */}
            <View className="flex-1 px-6 pt-6">
                {/* DATE PICKER: Horizontal list of available dates */}
                <Text className="text-white text-xl font-semibold mb-4">Chọn ngày</Text>
                <View>
                    <FlatList
                        data={days}
                        horizontal
                        showsHorizontalScrollIndicator={false}
                        keyExtractor={(_, index) => index.toString()}
                        renderItem={({ item, index }) => (
                            <TouchableOpacity
                                onPress={() => { setSelectedDate(item.fullISO); setSelectedTimeId(null); }}
                                className={`items-center justify-center mr-4 px-5 py-2 rounded-lg border ${selectedDate === item.fullISO
                                    ? 'bg-[#1e90ff] border-[#1e90ff]'
                                    : 'bg-[#272b50] border-white/5'
                                    }`}
                            >
                                <Text className={`text-xl font-bold ${selectedDate === item.fullISO ? 'text-white' : 'text-gray-200'}`}>
                                    {item.date}/{item.month}
                                </Text>

                                <Text className={`text-[12px] uppercase font-medium mt-1 ${selectedDate === item.fullISO ? 'text-white/80' : 'text-gray-400'}`}>
                                    {item.dayName}
                                </Text>
                            </TouchableOpacity>
                        )}
                    />
                </View>

                {/* SHOWTIME GRID: Grid of available hours */}
                <Text className="text-white text-xl font-semibold mt-8 mb-4">Suất chiếu</Text>
                <ScrollView showsVerticalScrollIndicator={false}>
                    <View className="flex-row flex-wrap gap-4 pb-10">
                        {timeSlots.map((slot, index) => (
                            <TouchableOpacity
                                key={slot.id ?? index.toString()}
                                onPress={() => setSelectedTimeId(slot.id)}
                                className={`rounded-lg px-4 py-3 items-center border ${selectedTimeId === slot.id
                                    ? 'bg-[#1e90ff] border-[#1e90ff]'
                                    : 'bg-[#272b50] border-white/10'
                                    }`}
                            >
                                <Text className={`font-bold text-xl ${selectedTimeId === slot.id ? 'text-white' : 'text-gray-300'}`}>
                                    {slot.time}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </View>
                </ScrollView>
            </View>

            {/* FIXED FOOTER: Navigation button */}
            <View className="p-5 bg-[#272b50] border-t border-white/5">
                <TouchableOpacity
                    className={`py-4 rounded-2xl items-center  ${selectedTimeId ? 'bg-[#1e90ff]' : 'bg-gray-600'
                        }`}
                    activeOpacity={0.8}
                    disabled={!selectedTimeId}
                    onPress={() => router.push({ pathname: '/booking', params: { showtimeId: selectedTimeId } })}
                >
                    <Text className="text-white font-bold text-lg uppercase">
                        Tiếp tục
                    </Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

export default SelectShowtime;