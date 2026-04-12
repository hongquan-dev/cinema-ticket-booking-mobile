import React from 'react';
import { Image, ImageSourcePropType, Text, TouchableOpacity, View } from 'react-native';

interface MovieCardProps {
    imageSource: ImageSourcePropType;
    title: string;
    version: string;
    duration: string;
    date: string;
    ageRating: number;
    width?: number;
    onPress?: () => void;
}

export const MovieCard: React.FC<MovieCardProps> = ({
    imageSource,
    title,
    version,
    duration,
    date,
    ageRating,
    width = 160,
    onPress,
}) => {
    return (
        <TouchableOpacity
            onPress={onPress}
            activeOpacity={0.8}
            style={{ width }}
            className="mb-4"
        >
            {/* Poster Image Container */}
            <View style={{
                width: '100%',
                height: width * 1.6,
                borderRadius: 12,
                overflow: 'hidden',
                backgroundColor: '#1a1d3d'
            }}>
                <Image
                    source={imageSource}
                    className="w-full h-full"
                    resizeMode="cover"
                />
            </View>

            {/* Movie Information Details */}
            <View className="mt-3 px-1 gap-2">
                {/* Title and Subtitle Section */}
                <Text
                    className="text-white text-[16px]"
                >
                    {title} - T{ageRating}
                </Text>

                {/* Technical Version Badge (e.g., 2D) */}
                <View className="flex-row items-center">
                    <View className="border border-[#F472B6] rounded px-1.5 py-0.5">
                        <Text className="text-[#F472B6] font-bold text-[14px]">
                            {version}
                        </Text>
                    </View>
                </View>

                {/* Runtime and Screening Date */}
                <Text className="text-gray-400 text-[16px] font-medium">
                    {duration} - {new Date(date).toLocaleDateString('vi-VN')}
                </Text>
            </View>
        </TouchableOpacity>
    );
};