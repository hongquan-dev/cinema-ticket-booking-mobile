import { Ionicons } from '@expo/vector-icons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { ActivityIndicator, Image, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import { PostLabel } from '../../src/enums/postEnums';

const NewsDetails = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const { id, title, description, content, thumbnailUrl, createAt, category } = params;

    const formatDate = (dateString: any) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')} ${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
    };


    if (!id) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />

            {/* Custom Header */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Chi tiết tin tức</Text>
                <View className="w-[28px]" />
            </View>

            <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
                {/* News Thumbnail Banner */}
                <View className="w-full h-64 bg-gray-800">
                    <Image
                        source={{ uri: thumbnailUrl as string }}
                        className="w-full h-full"
                        resizeMode="cover"
                    />
                </View>

                {/* Content Container */}
                <View className="px-4 py-5">
                    {/* Category Tag & Time */}
                    <View className="flex-row items-center mb-3">
                        <View className="bg-pink-600 px-2 py-1 rounded">
                            <Text className="text-white text-[14px] font-bold uppercase">
                                {PostLabel[category as keyof typeof PostLabel] || 'Tin tức'}
                            </Text>
                        </View>
                    </View>

                    {/* News Title */}
                    <Text className="text-white text-xl font-bold leading-7 mb-4">
                        {(title as string)?.toUpperCase()}
                    </Text>

                    {/* Divider */}
                    <View className="h-[1px] bg-gray-700 w-full mb-2" />

                    {/* Description (Abstract) */}
                    {description && (
                        <Text className="text-gray-300 text-[15px] font-semibold italic leading-6 mb-4">
                            {description}
                        </Text>
                    )}

                    {/* Main Content Body */}
                    <Text className="text-gray-300 text-[15px] leading-6 text-justify">
                        {content}
                    </Text>

                    <View className="flex-row justify-end w-full mt-2">
                        <Text className="text-gray-400 text-[14px] italic">
                            {formatDate(createAt)}
                        </Text>
                    </View>
                </View>
            </ScrollView>
        </View>
    );
};

export default NewsDetails;