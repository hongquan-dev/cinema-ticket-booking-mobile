import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

interface PostCardProps {
    title: string;
    time: string;
    imageUri: string;
    onPress?: () => void;
}

export const PostCard = ({ title, time, imageUri, onPress }: PostCardProps) => {
    return (
        <TouchableOpacity
            activeOpacity={0.7}
            onPress={onPress}
            className="flex-row items-start px-4 py-4 border-b border-gray-600 bg-[#272b50]"
        >
            {/* Thumbnail Image */}
            <View className="w-28 h-28 overflow-hidden bg-gray-700">
                <Image
                    source={{ uri: imageUri }}
                    className="w-full h-full"
                    resizeMode="cover"
                />
            </View>

            {/* Content */}
            <View className="flex-1 ml-4 mt-2">
                <Text
                    className="text-white text-[15px] font-semibold leading-5 mb-2"
                    numberOfLines={3}
                >
                    {title.toUpperCase()}
                </Text>

                <Text className="text-gray-400 text-[14px]">
                    {time}
                </Text>
            </View>
        </TouchableOpacity>
    );
};