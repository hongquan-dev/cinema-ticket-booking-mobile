import { Ionicons } from '@expo/vector-icons';
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
            className="flex-row items-center h-[110px] border rounded-xl border-gray-700 bg-[#272b50] mb-2 overflow-hidden"
        >
            {/* Left Image Section with Diagonal Slash */}
            <View className="w-36 h-full relative bg-gray-700">
                <Image
                    source={{ uri: imageUri }}
                    className="w-full h-full"
                    resizeMode="cover"
                />
            
                <View 
                    style={{
                        position: 'absolute',
                        right: 0,
                        bottom: 0,
                        width: 24,
                        height: '100%',
                        borderStyle: 'solid',
                        borderRightWidth: 20,
                        borderBottomWidth: 128, 
                        borderRightColor: '#272b50',
                        borderBottomColor: 'transparent',
                    }}
                />
            </View>

            {/* Content Area */}
            <View className="flex-1 justify-center pr-2">
                <Text
                    className="text-white text-[14px] font-semibold leading-5 mb-1.5 ml-1"
                    numberOfLines={2}
                >
                    {title.toUpperCase()}
                </Text>

                <Text className="text-gray-400 text-[13px] ml-2">
                    {time}
                </Text>
            </View>

            <View className="absolute bottom-4 right-3">
                <Ionicons name="chevron-forward" size={20} color="#9ca3af" />
            </View>
        </TouchableOpacity>
    );
};