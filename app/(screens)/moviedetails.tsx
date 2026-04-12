import { Ionicons } from '@expo/vector-icons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useEffect, useRef } from 'react';
import { ActivityIndicator, Animated, Dimensions, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';

const { width, height } = Dimensions.get('window');
const POSTER_HEIGHT = height * 0.7;

const MovieDetails = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const isBooking = params.isBooking === 'true'

    const fadeAnim = useRef(new Animated.Value(0)).current;

    useEffect(() => {
        Animated.timing(fadeAnim, {
            toValue: 1,
            duration: 1000,
            useNativeDriver: true,
        }).start();
    }, []);

    const movieData = {
        id: params.id as string || '',
        roomType: params.roomType as string || '',
        title: params.title as string || '',
        poster: params.poster as string || '',
        version: params.version as string || '',
        ageRating: params.ageRating as string || '',
        releaseDate: params.releaseDate || '',
        duration: params.duration || '',
        description: params.description || '',
        genres: Array.isArray(params.genres) ? params.genres : (typeof params.genres === 'string' ? params.genres.split(',') : []),
        directors: Array.isArray(params.directors) ? params.directors : (typeof params.directors === 'string' ? params.directors.split(',') : []),
        actors: Array.isArray(params.actors) ? params.actors : (typeof params.actors === 'string' ? params.actors.split(',') : []),
    };

    const handleBooking = (movieId: string, roomType: string) => {
        if (!movieId || !roomType) return;
        router.push({
            pathname: '/selectshowtime',
            params: {
                movieId: movieId,
                roomType: roomType,
            },
        });
    }

    if (!movieData) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#1a1d3d]">
            <StatusBar barStyle="light-content" />

            {/* 1. Background Poster (Fixed) */}
            <Animated.Image
                source={{ uri: movieData.poster as string }}
                style={{
                    position: 'absolute',
                    top: 0,
                    width: width,
                    height: POSTER_HEIGHT,
                    opacity: fadeAnim,
                    backgroundColor: 'white',
                }}
                resizeMode="cover"
            />

            {/* 2. Custom Header */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Thông tin phim</Text>
                <View className="w-[28px]" />
            </View>

            {/* 3. Main Container */}
            <View className="flex-1">
                {/* Spacer */}
                <View style={{ height: POSTER_HEIGHT * 0.45 }} />

                {/* Movie Detail Container */}
                <View className="flex-1 bg-[#272b50] rounded-t-[25px] px-[25px] pt-[25px] shadow-black shadow-offset-[0px/-10px] shadow-opacity-30 shadow-radius-10 elevation-20">

                    <View className="flex-row justify-between items-start mb-5">
                        <View className="flex-1 mr-[10px]">
                            <Text className="text-white text-2xl uppercase font-semibold">
                                {movieData.title}
                            </Text>
                            <View className="border border-[#F472B6] self-start rounded-md px-3 py-1 mt-3">
                                <Text className="text-[#F472B6] font-bold text-lg">{movieData.version}</Text>
                            </View>
                        </View>

                        <TouchableOpacity
                            activeOpacity={0.8}
                            className="bg-[#1e90ff] px-6 py-4 rounded-xl items-center justify-center elevation-8 shadow-[#1e90ff] shadow-offset-[0px/4px] shadow-opacity-30 shadow-radius-5"
                            onPress={() => handleBooking(movieData.id, movieData.roomType)}
                        >
                            {isBooking ? (
                                <Text className="text-white font-bold text-[18px]">ĐẶT VÉ</Text>
                            ) : (
                                <Text className="text-white font-bold text-[18px]">SẮP CHIẾU</Text>
                            )}
                        </TouchableOpacity>
                    </View>

                    <View className="h-[1px] bg-gray-500/30 w-full mb-4" />

                    <ScrollView
                        className="flex-1"
                        showsVerticalScrollIndicator={false}
                        overScrollMode="never"
                    >
                        <Text className="text-gray-300 text-[17px] leading-6 text-justify">
                            {movieData.description}
                        </Text>

                        <View className="h-[1px] bg-gray-500/30 w-full mt-5 mb-5" />

                        {/* Technical Specifications Section */}
                        <View className="gap-y-5 pb-10 pl-10 pr-5">
                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Kiểm duyệt</Text>
                                <Text className="text-white text-lg flex-1">{movieData.ageRating}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Khởi chiếu</Text>
                                <Text className="text-white text-lg flex-1">{movieData.releaseDate}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Thể loại</Text>
                                <Text className="text-white text-lg flex-1 leading-6">{movieData.genres.join(', ')}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Đạo diễn</Text>
                                <Text className="text-white text-lg flex-1 leading-6">{movieData.directors.join(', ')}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Diễn viên</Text>
                                <Text className="text-white text-lg flex-1 leading-6">{movieData.actors.join(', ')}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Thời lượng</Text>
                                <Text className="text-white text-lg flex-1 leading-6">{movieData.duration}</Text>
                            </View>

                            <View className="flex-row items-start">
                                <Text className="text-gray-400 font-semibold text-lg w-32">Ngôn ngữ</Text>
                                <Text className="text-white text-lg flex-1 leading-6">Phụ đề Tiếng Việt</Text>
                            </View>
                        </View>

                        <View className="h-10"></View>
                    </ScrollView>
                </View>
            </View>
        </View>
    );
};

export default MovieDetails;