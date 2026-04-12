import React from 'react';
import { Text, View } from 'react-native';

export default function TicketsPage() {
    return (
        <View className="flex-1 bg-[#060b11] justify-center items-center">
            <View>
                <Text className="text-2xl font-bold text-white">Trang vé đã mua</Text>
                <Text className="text-gray-400 mt-2 text-center">
                    Chào mừng bạn đến với NCC Cinema
                </Text>
            </View>
        </View>
    );
}