import useNotification from '@/src/hooks/useNotification';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, Image, ScrollView, StatusBar, Text, TextInput, TouchableOpacity, View } from 'react-native';
import userApi from '../../src/services/userApi';

const ChangePassword = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const [userId, setUserId] = useState<string>('');

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const [fetching, setFetching] = useState(true);
    const { notify, NotificationComponent } = useNotification();

    const isFormValid =
        oldPassword.length > 0 &&
        newPassword.length > 0 &&
        confirmPassword.length > 0;

    useFocusEffect(
        useCallback(() => {
            const fetchUserData = async () => {
                try {
                    setFetching(true);
                    const storeUserId = await AsyncStorage.getItem('user_id');

                    if (storeUserId) {
                        setUserId(storeUserId);
                    }
                } catch (error) {
                } finally {
                    setFetching(false);
                }
            };

            fetchUserData();
        }, [])
    );

    const hanleEditAvatar = async () => {
        await notify("Tính năng", "Tính năng này sẽ sớm ra mắt", "info");
    }

    const handleSave = async () => {
        if (newPassword !== confirmPassword) {
            await notify("Lỗi", "Mật khẩu mới và xác nhận mật khẩu không khớp!", "error");
            return;
        }

        // Password Strength (Min 8 chars, 1 letter, 1 number, 1 special char)
        const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        if (!strongPasswordRegex.test(newPassword)) {
            notify(
                "Mật khẩu yếu",
                "Mật khẩu cần tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
                "warning"
            );
            return;
        }

        try {
            const passwordData = {
                oldPassword,
                newPassword
            };

            const response: any = await userApi.changePassword(userId, passwordData);

            await notify("Thành công", "Đổi mật khẩu thành công!", "success");
            router.back();
        } catch (error: any) {
            if (error.toString() === "Old password is incorrect") {
                await notify("Thất bại", "Mật khẩu cũ không chính xác", "error");
            }
            else {
                await notify("Thất bại", "Đổi mật khẩu thất bại", "error");
            }
        }
    };

    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isConfirmPasswordVisible, setIsConfirmPasswordVisible] = useState(false);
    const togglePasswordVisibility = () => {
        setIsPasswordVisible(!isPasswordVisible);
    };
    const toggleConfirmPasswordVisibility = () => {
        setIsConfirmPasswordVisible(!isConfirmPasswordVisible);
    };

    const InputLabel = ({ label }: { label: string }) => (
        <Text className="text-white text-[16px] font-medium mb-2 mt-4">{label}</Text>
    );

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />
            {NotificationComponent}

            {/* Custom Header */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Đổi mật khẩu</Text>
                <View className="w-[28px]" />
            </View>

            {fetching ? (
                <View className="flex-1 bg-[#272b50] justify-center items-center">
                    <ActivityIndicator size="large" color="#1e90ff" />
                </View>
            ) : (
                <ScrollView className="flex-1 px-5" showsVerticalScrollIndicator={false}>
                    {/* --- Avatar --- */}
                    <View className="items-center">
                        <View className="relative w-44 h-44">
                            <Image
                                source={require('../../assets/images/Quannn.jpg')}
                                className="w-full h-full rounded-full border-4 border-gray-600"
                                resizeMode="cover"
                            />

                            <TouchableOpacity
                                className="absolute bottom-0 right-[5px] bg-[#3bacef] w-11 h-11 rounded-full items-center justify-center shadow-lg"
                                activeOpacity={0.8}
                                onPress={hanleEditAvatar}
                            >
                                <Ionicons name="camera" size={24} color="white" />
                            </TouchableOpacity>
                        </View>
                    </View>

                    {/* Old password Row */}
                    <InputLabel label="Mật khẩu cũ" />
                    <View className="bg-gray-50 border border-gray-100 rounded-xl w-full flex-row justify-between items-center relative">
                        <TextInput
                            className="px-5 py-4 w-full"
                            secureTextEntry={!isPasswordVisible}
                            value={oldPassword}
                            onChangeText={setOldPassword}
                            placeholder="Mật khẩu cũ"
                        />
                        <TouchableOpacity onPress={togglePasswordVisibility} className="absolute right-3">
                            <Ionicons name={isPasswordVisible ? "eye" : "eye-off"} size={22} color="gray" />
                        </TouchableOpacity>
                    </View>

                    {/* New password */}
                    <InputLabel label="Mật khẩu mới" />
                    <View className="bg-gray-50 border border-gray-100 rounded-xl w-full flex-row justify-between items-center relative">
                        <TextInput
                            className="px-5 py-4 w-full"
                            secureTextEntry={!isConfirmPasswordVisible}
                            value={newPassword}
                            onChangeText={setNewPassword}
                            placeholder="Mật khẩu mới"
                        />
                        <TouchableOpacity onPress={toggleConfirmPasswordVisibility} className="absolute right-3">
                            <Ionicons name={isConfirmPasswordVisible ? "eye" : "eye-off"} size={22} color="gray" />
                        </TouchableOpacity>
                    </View>

                    {/* Confirm password */}
                    <InputLabel label="Xác nhận mật khẩu" />
                    <View className="bg-gray-50 border border-gray-100 rounded-xl w-full flex-row justify-between items-center relative">
                        <TextInput
                            className="px-5 py-4 w-full"
                            secureTextEntry={!isConfirmPasswordVisible}
                            value={confirmPassword}
                            onChangeText={setConfirmPassword}
                            placeholder="Xác nhận mật khẩu"
                        />
                    </View>
                </ScrollView>
            )}

            {/* Bottom Save Button */}
            {!fetching && (
                <View className="px-5 pb-6 bg-[#272b50]">
                    <TouchableOpacity
                        onPress={handleSave}
                        disabled={!isFormValid}
                        className={`py-4 rounded-xl items-center ${isFormValid ? 'bg-[#1e90ff]' : 'bg-gray-400'}`}
                    >
                        <Text className={`text-white font-bold text-xl`}>
                            Cập nhật
                        </Text>
                    </TouchableOpacity>
                </View>
            )}
        </View>
    );
};

export default ChangePassword;