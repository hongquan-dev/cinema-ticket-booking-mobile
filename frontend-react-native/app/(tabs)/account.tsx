import { Ionicons, MaterialCommunityIcons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { Linking, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import useConfirm from '../../src/hooks/useConfirm';
import useNotification from '../../src/hooks/useNotification';
import authService from '../../src/services/authService';

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
        activeOpacity={0.6}
        className={`flex-row items-center px-4 py-3.5 rounded-lg mb-2 border border-white/5 hover:bg-[#252d4a] transition-colors`}
    >
        <View className={`w-12 h-12 rounded-lg items-center justify-center ${iconBgColor}`}>
            {icon}
        </View>
        <View className="flex-1 ml-4 flex-row justify-between items-center">
            <View className="flex-1">
                <Text className="text-white text-lg font-semibold">{title}</Text>
                {subtitle && <Text className="text-gray-400 text-[13px]">{subtitle}</Text>}
            </View>
            <Ionicons name="chevron-forward" size={20} color="#8E8E93" />
        </View>
    </TouchableOpacity>
);

// Section Header Component
const SectionHeader = ({ title, isSmallMargin = false }: { title: string, isSmallMargin?: boolean }) => (
    <View className={isSmallMargin ? "mt-1" : "mt-4"}>
        <View className="flex-row items-center">
            <View className="w-1 h-6 bg-[#1e90ff] rounded-full mr-2" />
            <Text className="text-white font-bold text-[18px] tracking-wide">
                {title}
            </Text>
        </View>
        <View className="h-0.5 bg-gradient-to-r from-[#1e90ff]/30 to-transparent mt-2 rounded-full" />
    </View>
);

export default function AccountPage() {
    const router = useRouter();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const { confirm, ConfirmComponent } = useConfirm();
    const { notify, NotificationComponent } = useNotification();

    useFocusEffect(
        useCallback(() => {
            checkLoginStatus();
        }, [])
    );

    // Helper function to clear all user-related data from AsyncStorage
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

    const checkLoginStatus = async () => {
        try {
            const refreshToken = await AsyncStorage.getItem('refresh_token');
            if (!refreshToken) {
                await clearUserData();
                return;
            }

            const response: any = await authService.refreshToken(refreshToken);
            if (response && response.accessToken) {
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

    const handleCallPress = async (phoneNumber: string) => {
        const cleanedNumber = phoneNumber.replace(/\s/g, '');
        const url = `tel:${cleanedNumber}`;

        try {
            await Linking.openURL(url);
        } catch (error) {
            console.error("Failed to open dialer:", error);
        }
    };

    const handleDeleteAccount = async () => {
        await notify("Thông báo", "Tính năng này sẽ sớm ra mắt", "info");
    };

    const handleGroupBooking = async () => {
        await notify("Thông báo", "Tính năng này sẽ sớm ra mắt", "info");
    };

    return (
        <View className="flex-1 bg-[#272b50] pt-14">
            <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
                <View className="px-4 pb-2">
                    <StatusBar barStyle="light-content" />
                    {ConfirmComponent}
                    {NotificationComponent}

                    {isLoggedIn && (
                        <View>
                            {/* Top Section - General */}
                            <SectionHeader title="Cá nhân" isSmallMargin={true} />
                            <MenuItem
                                icon={<Ionicons name="notifications" size={22} color="white" />}
                                title="Thông báo"
                                iconBgColor="bg-pink-600"
                                onPress={() => router.push('/(account)/notifications')}
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="card-account-details" size={22} color="white" />}
                                title="Thẻ thành viên U22"
                                iconBgColor="bg-orange-500"
                                onPress={() => router.push('/(account)/membercard')}
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
                                onPress={() => router.push('/(account)/profile')}
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="form-textbox-password" size={22} color="white" />}
                                title="Đổi mật khẩu"
                                iconBgColor="bg-amber-900"
                                onPress={() => router.push('/(account)/changepassword')}
                            />
                            <MenuItem
                                icon={<MaterialCommunityIcons name="account-remove" size={22} color="white" />}
                                title="Xóa tài khoản"
                                iconBgColor="bg-red-500"
                                onPress={handleDeleteAccount}
                            />
                        </View>
                    )}

                    {/* Support Section */}
                    <SectionHeader title="Hỗ trợ" />
                    <MenuItem
                        icon={<Ionicons name="call" size={22} color="white" />}
                        title="Hotline"
                        subtitle="0865.205.608"
                        iconBgColor="bg-indigo-600"
                        onPress={() => handleCallPress('0865205608')}
                    />
                    <MenuItem
                        icon={<MaterialCommunityIcons name="chat" size={22} color="white" />}
                        title="Zalo"
                        subtitle="Chat với chúng tôi"
                        iconBgColor="bg-blue-400"
                        onPress={() => {
                            Linking.openURL('https://zalo.me/0865205608').catch(err =>
                                console.error("Couldn't load page", err)
                            );
                        }}
                    />

                    {isLoggedIn && (
                        // Support Section
                        <View>
                            <MenuItem
                                icon={<Ionicons name="people" size={22} color="white" />}
                                title="Đặt vé nhóm, tập thể"
                                iconBgColor="bg-slate-500"
                                onPress={handleGroupBooking}
                            />
                        </View>
                    )}

                    {/* Information Section */}
                    <SectionHeader title="Thông tin" />
                    <MenuItem
                        icon={<Ionicons name="shield-checkmark" size={22} color="white" />}
                        title="Điều khoản & Chính sách"
                        iconBgColor="bg-sky-600"
                        onPress={() => router.push('/(account)/terms_policy')}
                    />
                    <MenuItem
                        icon={<MaterialCommunityIcons name="office-building" size={22} color="white" />}
                        title="Thông tin doanh nghiệp"
                        iconBgColor="bg-green-600"
                        onPress={() => router.push('/(account)/business_info')}
                    />

                    {isLoggedIn && (
                        <MenuItem
                            icon={<Ionicons name="log-out" size={22} color="white" />}
                            title="Đăng xuất"
                            iconBgColor="bg-purple-600"
                            onPress={handleLogout}
                        />
                    )}

                    {/* --- If not logged in --- */}
                    {!isLoggedIn && (
                        <View className="flex-row gap-3 px-0 mt-6 mb-3">
                            {/* Primary Button: Login */}
                            <TouchableOpacity
                                onPress={() => router.push('/(auth)/login')}
                                activeOpacity={0.8}
                                className="bg-[#3bacef] flex-1 py-3 rounded-xl items-center shadow-md shadow-blue-500/20"
                            >
                                <Text className="text-white font-semibold text-lg">Đăng nhập</Text>
                            </TouchableOpacity>

                            {/* Secondary Button: Signup */}
                            <TouchableOpacity
                                onPress={() => router.push('/(auth)/signup')}
                                activeOpacity={0.8}
                                className="bg-slate-50 border border-slate-200 flex-1 py-3 rounded-xl items-center"
                            >
                                <Text className="text-slate-700 font-semibold text-lg">Đăng ký</Text>
                            </TouchableOpacity>
                        </View>
                    )}

                    {/* Footer Info */}
                    <View className="mt-2 items-center border-t border-white/10 pt-2">
                        <Text className="text-gray-400 text-sm font-semibold">Phiên bản 1.0.0</Text>
                        <Text className="text-gray-500 text-[11px] text-center leading-relaxed">
                            Ứng dụng được phát triển bởi Nguyen Hong Quan
                        </Text>
                    </View>
                </View>
            </ScrollView>
        </View>
    );
}