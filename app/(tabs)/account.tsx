import { Ionicons, MaterialCommunityIcons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import authService from '../../src/services/authService';
import useConfirm from '../../src/hooks/useConfirm';

// Reusable Menu Item Component
const MenuItem = ({
    icon,
    title,
    subtitle,
    iconBgColor,
    isLast = false,
    onPress
}: {
    icon: any,
    title: string,
    subtitle?: string,
    iconBgColor: string,
    isLast?: boolean,
    onPress?: () => void
}) => (
    <TouchableOpacity
        onPress={onPress}
        className={`flex-row items-center py-4 ${!isLast ? 'border-b border-gray-700' : ''}`}
    >
        <View className={`w-12 h-12 rounded-md items-center justify-center ${iconBgColor}`}>
            {icon}
        </View>
        <View className="flex-1 ml-4 flex-row justify-between items-center">
            <Text className="text-white text-xl">{title}</Text>
            {subtitle && <Text className="text-gray-400 text-lg">{subtitle}</Text>}
        </View>
    </TouchableOpacity>
);

// Section Header Component
const SectionHeader = ({ title }: { title: string }) => (
    <Text className="text-white font-bold text-xl mt-6 mb-2 uppercase tracking-wider">
        {title}
    </Text>
);

export default function AccountPage() {
    const router = useRouter();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const { confirm, ConfirmComponent } = useConfirm();

    const clearUserData = async () => {
        const keys = [
            'access_token',
            'refresh_token',
            'user_id',
            'user_name',
            'user_email',
            'user_phone'
        ];
        await AsyncStorage.multiRemove(keys);
        setIsLoggedIn(false);
    };

    useFocusEffect(
        useCallback(() => {
            checkLoginStatus();
        }, [])
    );

    const checkLoginStatus = async () => {
        try {
            const refreshToken = await AsyncStorage.getItem('refresh_token');

            if (!refreshToken) {
                await clearUserData();
                return;
            }

            const response: any = await authService.refreshToken(refreshToken);

            if (response && response.accessToken) {
                // await AsyncStorage.setItem('access_token', response.accessToken);
                setIsLoggedIn(true);
            } else {
                await clearUserData();
            }
        } catch (error) {
            await clearUserData();
        } finally {
            setIsLoading(false);
        }
    };

    const handleLogout = async () => {
        const isConfirm = await confirm("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất?", "info");
        if (!isConfirm) return;

        try {
            await authService.logout();
        } catch (e) {
            console.log("Logout API error:", e);
        } finally {
            await clearUserData();
        }
    };

    if (isLoading) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }
    return (
        <View className="flex-1 bg-[#272b50] pt-14">
            <ScrollView className="flex-1 bg-[#272b50]" showsVerticalScrollIndicator={false}>
                {/* Header Padding for Status Bar */}
                <View className="px-5 pb-2">
                    <StatusBar barStyle="light-content" />
                    {ConfirmComponent}

                    {isLoggedIn && (
                        <View>
                            {/* Top Section - General */}
                            <MenuItem
                                icon={<Ionicons name="notifications" size={22} color="white" />}
                                title="Thông báo"
                                iconBgColor="bg-pink-600"
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="card-account-details" size={22} color="white" />}
                                title="Thẻ thành viên U22"
                                iconBgColor="bg-orange-500"
                                isLast={true}
                            />
                        </View>
                    )}

                    {isLoggedIn && (
                        <View>
                            {/* Account Section */}
                            <SectionHeader title="Tài khoản" />
                            <MenuItem
                                icon={<Ionicons name="person" size={22} color="white" />}
                                title="Thông tin tài khoản"
                                iconBgColor="bg-blue-500"
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="form-textbox-password" size={22} color="white" />}
                                title="Đổi mật khẩu"
                                iconBgColor="bg-amber-900"
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="account-remove" size={22} color="white" />}
                                title="Xóa tài khoản"
                                iconBgColor="bg-red-500"
                                isLast={true}
                            />
                        </View>
                    )}
                    <SectionHeader title="Hỗ trợ" />
                    <MenuItem
                        icon={<Ionicons name="call" size={22} color="white" />}
                        title="Hotline"
                        subtitle="0865.205.608"
                        iconBgColor="bg-indigo-600"
                    />
                    <MenuItem
                        icon={<MaterialCommunityIcons name="chat" size={22} color="white" />}
                        title="Zalo"
                        subtitle="https://zalo.me/hongquan_dev"
                        iconBgColor="bg-blue-400"
                    />

                    {isLoggedIn && (
                        // Support Section
                        < View >
                            < MenuItem
                                icon={<Ionicons name="people" size={22} color="white" />}
                                title="Đặt vé nhóm, tập thể"
                                iconBgColor="bg-slate-500"
                                isLast={true}
                            />
                        </View>
                    )}


                    {/* Setting Section */}
                    <SectionHeader title="Cài đặt" />
                    <MenuItem
                        icon={<Ionicons name="shield-checkmark" size={22} color="white" />}
                        title="Điều khoản & chính sách"
                        iconBgColor="bg-blue-500"
                    />
                    <MenuItem
                        icon={<Ionicons name="business" size={22} color="white" />}
                        title="Thông tin doanh nghiệp"
                        iconBgColor="bg-green-600"
                    />

                    {isLoggedIn && (
                        <MenuItem
                            icon={<Ionicons name="log-out" size={22} color="white" />}
                            title="Đăng xuất"
                            iconBgColor="bg-purple-600"
                            isLast={true}
                            onPress={handleLogout}
                        />
                    )}

                    {/* --- If not logged in --- */}
                    {!isLoggedIn && (
                        <View className="flex-row justify-between px-5 mt-8 mb-5">
                            <TouchableOpacity
                                onPress={() => router.push('/(auth)/login')}
                                className="bg-[#3bacef] flex-1 mr-2 py-3 rounded-xl items-center shadow-sm"
                            >
                                <Text className="text-white font-bold text-lg">Đăng nhập</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                onPress={() => router.push('/(auth)/signup')}
                                className="bg-transparent border border-[#3bacef] flex-1 ml-2 py-3 rounded-xl items-center"
                            >
                                <Text className="text-[#3bacef] font-bold text-lg">Đăng ký</Text>
                            </TouchableOpacity>
                        </View>
                    )}

                    {/* Footer Info */}
                    <View className="mt-2 items-center">
                        <Text className="text-gray-400 text-sm">Phiên bản 1.0.0</Text>
                        <Text className="text-gray-500 text-sm">
                            Ứng dụng được phát triển bởi Nguyen Hong Quan
                        </Text>
                    </View>
                </View>
            </ScrollView >
        </View>
    );
}