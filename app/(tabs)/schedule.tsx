import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, FlatList, ScrollView, Text, TouchableOpacity, View } from 'react-native';
import ShowtimeCard from '../../src/components/card/ShowtimeCard';
import showtimeApi from '../../src/services/showtimeApi';

export default function SchedulePage() {
    const router = useRouter();
    const [currentDate, setCurrentDate] = useState<string[]>([]);
    const [cinemaId, setCinemaId] = useState<string>('');
    const [selectedDate, setSelectedDate] = useState<string>('');
    const [showtimeGroups, setShowtimeGroups] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    useFocusEffect(
        useCallback(() => {
            let isActive = true;

            const loadAllData = async () => {
                setLoading(true);
                try {
                    const storedDate = await AsyncStorage.getItem('currentDate');
                    const storedCinemaId = await AsyncStorage.getItem('cinemaId');

                    if (storedDate && storedCinemaId && isActive) {
                        const dates = JSON.parse(storedDate);
                        const cinemaId = JSON.parse(storedCinemaId);

                        setCurrentDate(dates);
                        setCinemaId(cinemaId);

                        const targetDate = selectedDate || dates[0];
                        if (!selectedDate) setSelectedDate(dates[0]);

                        const response = await showtimeApi.getGrouped(cinemaId, targetDate);
                        if (isActive) setShowtimeGroups(response.data || []);
                    }
                } catch (error) {
                    console.error("Lỗi:", error);
                } finally {
                    if (isActive) setLoading(false);
                }
            };

            loadAllData();

            return () => {
                isActive = false;
            };
        }, [selectedDate])
    );

    const formatTime = (isoString: string) => {
        const date = new Date(isoString);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return `${date.getDate()}-${date.getMonth() + 1}-${date.getFullYear()}`;
    };

    if (!currentDate.length || !cinemaId) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#1a1d3d]">
            {/* Header Tabs */}
            <View className="bg-[#272b50] pt-16 pb-2">
                <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ paddingHorizontal: 18 }}>
                    {currentDate.map((date, index) => (
                        <TouchableOpacity
                            key={index}
                            onPress={() => setSelectedDate(date)}
                            className={`mr-6 pb-1 border-b-2 ${selectedDate === date ? 'border-pink-500' : 'border-transparent'}`}
                        >
                            <Text className={`text-xl font-semibold ${selectedDate === date ? 'text-white' : 'text-gray-400'}`}>
                                {formatDate(date)}
                            </Text>
                        </TouchableOpacity>
                    ))}
                </ScrollView>
            </View>

            {/* Showtime Card */}
            {loading ? (
                <View className="flex-1 justify-center items-center">
                    <ActivityIndicator size="large" color="#1e90ff" />
                </View>
            ) : (
                <FlatList
                    data={showtimeGroups}
                    keyExtractor={(item, index) => `${item.movie.id}-${item.roomType}-${index}`}
                    renderItem={({ item }) => (
                        <ShowtimeCard
                            title={item.movie.movieName}
                            ageRating={item.movie.ageRating}
                            version={item.roomType}
                            poster={item.movie.posterUrl}
                            showtimes={item.showtimes.map((s: any) => formatTime(s.startTime))}
                            onPressShowtime={(time) => {
                                const selectedSlot = item.showtimes.find((s: any) => formatTime(s.startTime) === time);
                                router.push({
                                    pathname: '/(screens)/booking',
                                    params: {
                                        showtimeId: selectedSlot?.id,
                                    }
                                });
                            }}
                        />
                    )}
                    contentContainerStyle={{ paddingBottom: 40 }}
                    ListEmptyComponent={
                        <View className="mt-10 items-center">
                            <Text className="text-gray-400 text-xl">Không có suất chiếu cho ngày này</Text>
                        </View>
                    }
                />
            )}
        </View>
    );
}