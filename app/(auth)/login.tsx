import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useRouter } from 'expo-router';

import React, { useState } from 'react';
import { Image, ScrollView, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import authService from '../../src/api/authService';
import useNotification from '../../src/hooks/useNotification';

export default function LoginScreen() {
    const router = useRouter();
    const { notify, NotificationComponent } = useNotification();

    // UI States
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);

    // Form States
    const [form, setForm] = useState({
        username: '',
        password: '',
        rememberMe: false
    });

    const handleToggleRememberMe = () => {
        const newValue = !rememberMe;
        setRememberMe(newValue);
        setForm({ ...form, rememberMe: newValue });
    };

    const togglePasswordVisibility = () => {
        setIsPasswordVisible(!isPasswordVisible);
    };

    const handleLogin = async () => {
        // Validation check
        if (!form.username.trim() || !form.password) {
            notify("Thiếu thông tin", "Vui lòng nhập tên đăng nhập và mật khẩu", "warning");
            return;
        }

        setIsSubmitting(true);
        try {
            // Call API via service
            const response: any = await authService.login(form);
            if (response.role === "ROLE_ADMIN") {
                notify("Thất bại", "Chưa hỗ trợ cho tài khoản của quản lí", "error");
                return;
            }

            // Save token to AsyncStorage
            if (response.accessToken && response.refreshToken) {
                await AsyncStorage.setItem('access_token', response.accessToken);
                await AsyncStorage.setItem('refresh_token', response.refreshToken);
            } else {
                console.error("Token not found in response:", response);
            }

            if (response.data) {
                await AsyncStorage.setItem('user_id', String(response.data.id));
                await AsyncStorage.setItem('user_name', String(response.data.username));
                await AsyncStorage.setItem('user_email', String(response.data.email));
                await AsyncStorage.setItem('user_phone', String(response.data.phoneNumber));

            }

            await notify("Thành công", "Hãy đặt vé ngay!", "success");
            router.back();
        } catch (error: any) {
            // Error from axios interceptor
            await notify("Đăng nhập thất bại", "Sai tài khoản hoặc mật khẩu", "error");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <SafeAreaView className="flex-1 bg-[#232946]" edges={['top']}>
            {NotificationComponent}

            <ScrollView
                contentContainerStyle={{ flexGrow: 1, justifyContent: 'center' }}
                showsVerticalScrollIndicator={false}
            >

                {/* Back Button */}
                <View className="px-4 py-2 absolute top-0 left-0 z-10">
                    <TouchableOpacity onPress={() => router.back()}>
                        <Ionicons name="chevron-back" size={28} color="white" />
                    </TouchableOpacity>
                </View>

                <View className="items-center px-6 py-10">

                    {/* Header Logo Section */}
                    <View className="flex-row items-center mb-8">
                        <View className="w-16 h-14 rounded-xl overflow-hidden shadow-lg bg-white">
                            <Image
                                source={require('../../assets/images/NCC_Logo.jpg')}
                                style={{ width: '100%', height: '100%' }}
                                resizeMode="cover"
                            />
                        </View>
                        <View className="ml-4">
                            <Text className="text-white font-bold text-[14px] uppercase tracking-tighter">
                                Trung tâm chiếu phim quốc gia
                            </Text>
                            <Text className="text-white text-[11px] opacity-70 uppercase">
                                National Cinema Center
                            </Text>
                        </View>
                    </View>

                    {/* Login Card */}
                    <View className="bg-white w-full rounded-[25px] p-7 shadow-2xl">
                        <Text className="text-center text-2xl font-bold text-gray-800 mb-8">Đăng nhập</Text>

                        {/* Username Input */}
                        <TextInput
                            placeholder="Tên đăng nhập"
                            className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-4 text-gray-800"
                            onChangeText={(v) => setForm({ ...form, username: v })}
                            autoCorrect={false}
                            autoCapitalize="none"
                        />

                        {/* Password Input */}
                        <View className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-4 flex-row justify-between items-center">
                            <TextInput
                                placeholder="Mật khẩu"
                                secureTextEntry={!isPasswordVisible}
                                className="flex-1 text-gray-800"
                                onChangeText={(v) => setForm({ ...form, password: v })}
                            />
                            <TouchableOpacity onPress={togglePasswordVisibility}>
                                <Ionicons name={isPasswordVisible ? "eye" : "eye-off"} size={20} color="gray" />
                            </TouchableOpacity>
                        </View>

                        {/* Remember Me & Forgot Password Row */}
                        <View className="flex-row justify-between items-center mb-8 px-1">
                            <TouchableOpacity
                                className="flex-row items-center"
                                onPress={() => handleToggleRememberMe()}
                            >
                                <Ionicons
                                    name={rememberMe ? "checkbox" : "square-outline"}
                                    size={20}
                                    color={rememberMe ? "#3bacef" : "gray"}
                                />
                                <Text className="ml-2 text-gray-600 text-sm">Nhớ mật khẩu</Text>
                            </TouchableOpacity>

                            <TouchableOpacity>
                                <Text className="text-[#3bacef] text-sm font-semibold">Quên mật khẩu?</Text>
                            </TouchableOpacity>
                        </View>

                        {/* Login Button */}
                        <TouchableOpacity
                            className="bg-[#3bacef] py-4 rounded-2xl shadow-md active:opacity-90 flex-row justify-center items-center mb-4"
                            onPress={handleLogin}
                            disabled={isSubmitting}
                        >
                            <Ionicons name="log-in-outline" size={24} color="white" style={{ marginRight: 10 }} />
                            <Text className="text-white text-center font-bold text-lg uppercase">
                                Đăng nhập
                            </Text>
                        </TouchableOpacity>

                        {/* Register Link Button */}
                        <View className="flex-row justify-center items-center mt-2">
                            <Text className="text-gray-500">Bạn chưa có tài khoản? </Text>
                            <TouchableOpacity onPress={() => router.push('/(auth)/signup')}>
                                <Text className="text-[#3bacef] font-bold">Đăng ký ngay</Text>
                            </TouchableOpacity>
                        </View>

                    </View>
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}