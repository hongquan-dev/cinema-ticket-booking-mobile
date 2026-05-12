import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import React from 'react';
import { ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';

const BusinessInfo = () => {
    const router = useRouter();

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />

            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Thông tin doanh nghiệp</Text>
                <View className="w-[28px]" />
            </View>

            <ScrollView className="flex-1 px-5 pt-4 pb-10" showsVerticalScrollIndicator={false}>
                <View className="bg-[#272b50] rounded-xl p-5 border border-gray-600 shadow-xl">
                    <Text className="text-white text-[18px] font-bold text-center mb-6 uppercase">
                        Trung tâm chiếu phim quốc gia
                    </Text>

                    <Text className="text-gray-300 text-[16px] leading-[26px] mb-4 text-justify">
                        <Text className="font-bold text-[#1e90ff]">Cơ quan chủ quản: </Text>
                        BỘ VĂN HÓA, THỂ THAO VÀ DU LỊCH
                    </Text>

                    <Text className="text-gray-300 text-[16px] leading-[26px] mb-4 text-justify">
                        Bản quyền thuộc Trung tâm Chiếu phim Quốc gia.
                    </Text>

                    <Text className="text-gray-300 text-[16px] leading-[26px] mb-4 text-justify">
                        <Text className="font-bold text-[#1e90ff]">Giấy phép số: </Text>
                        275/GP- TTĐT ngày 01/01/2026 - Chịu trách nhiệm: Nguyễn Hồng Quân – Giám đốc.
                    </Text>

                    <Text className="text-gray-300 text-[16px] leading-[26px] mb-6 text-justify">
                        <Text className="font-bold text-[#1e90ff]">Địa chỉ: </Text>
                        Số 87 Láng Hạ, Phường Ô Chợ Dừa, TP.Hà Nội - Điện thoại: 0865.205.608
                    </Text>

                    <View className="border-t border-gray-600 pt-4 mt-2">
                        <Text className="text-gray-400 text-[14px] text-center">
                            Copyright 2026. NCC All Rights Reservered. Dev by Nguyen Hong Quan
                        </Text>
                    </View>
                </View>
            </ScrollView>
        </View>
    );
};

export default BusinessInfo;
