import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { Image, ScrollView, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import authService from '../../src/api/authService';
import useNotification from '../../src/hooks/useNotification';

export default function SignUpScreen() {
    const router = useRouter();
    const { notify, NotificationComponent } = useNotification();

    // UI States
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Form States
    const [form, setForm] = useState({
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: ''
    });

    const togglePasswordVisibility = () => {
        setIsPasswordVisible(!isPasswordVisible);
    };

    const validateForm = () => {
        const { username, email, phone, password, confirmPassword } = form;

        // 1. Individual field presence check
        if (!username.trim()) {
            notify("Thiếu thông tin", "Tên đăng nhập bị thiếu", "warning");
            return false;
        }
        if (!email.trim()) {
            notify("Thiếu thông tin", "Email bị thiếu", "warning");
            return false;
        }
        if (!phone.trim()) {
            notify("Thiếu thông tin", "Số điện thoại bị thiếu", "warning");
            return false;
        }
        if (!password) {
            notify("Thiếu thông tin", "Mật khẩu bị thiếu", "warning");
            return false;
        }

        // 2. Email validation (Strictly @gmail.com)
        const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
        if (!gmailRegex.test(email)) {
            notify("Email không hợp lệ", "Email phải có định dạng @gmail.com", "warning");
            return false;
        }

        // 3. Phone validation (Must be exactly 10 digits)
        const phoneRegex = /^\d{10}$/;
        if (!phoneRegex.test(phone)) {
            notify("Số điện thoại lỗi", "Số điện thoại phải có đúng 10 chữ số và chỉ chứa số", "warning");
            return false;
        }

        // 4. Password Strength (Min 8 chars, 1 letter, 1 number, 1 special char)
        const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        if (!strongPasswordRegex.test(password)) {
            notify(
                "Mật khẩu yếu",
                "Mật khẩu cần tối thiểu 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt",
                "warning"
            );
            return false;
        }

        // 5. Match Check
        if (password !== confirmPassword) {
            notify("Lỗi mật khẩu", "Mật khẩu xác nhận không trùng khớp!", "error");
            return false;
        }

        return true;
    };

    const handleSignup = async () => {
        // if (!validateForm()) return;

        setIsSubmitting(true);
        try {
            const response: any = await authService.signup(form);

            await notify("Thành công", "Đăng ký thành công!", "success");
            router.back();
        } catch (error: any) {
            if (error === "Username already exists") {
                await notify("Đăng ký thất bại", "Tên đăng nhập đã tồn tại", "error");
            } else if (error === "Email already exists") {
                await notify("Đăng ký thất bại", "Email đã tồn tại", "error");
            } else {
                await notify("Đăng ký thất bại", error, "error");
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <SafeAreaView className="flex-1 bg-[#232946]" edges={['top']}>
            {/* Custom Dialog UI */}
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

                    {/* Registration Card */}
                    <View className="bg-white w-full rounded-[25px] p-7 shadow-2xl">
                        <Text className="text-center text-2xl font-bold text-gray-800 mb-6">Đăng ký</Text>

                        <TextInput
                            placeholder="Tên đăng nhập"
                            className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-4 text-gray-800"
                            onChangeText={(v) => setForm({ ...form, username: v })}
                            autoCorrect={false}
                            autoCapitalize="none"
                        />

                        <TextInput
                            placeholder="Email"
                            keyboardType="email-address"
                            autoCapitalize="none"
                            className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-4 text-gray-800"
                            onChangeText={(v) => setForm({ ...form, email: v })}
                        />

                        <TextInput
                            placeholder="Số điện thoại"
                            keyboardType="phone-pad"
                            maxLength={10}
                            className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-4 text-gray-800"
                            onChangeText={(v) => setForm({ ...form, phone: v })}
                        />

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

                        <View className="bg-gray-50 border border-gray-100 p-4 rounded-2xl w-full mb-8 flex-row justify-between items-center">
                            <TextInput
                                placeholder="Xác nhận mật khẩu"
                                secureTextEntry={!isPasswordVisible}
                                className="flex-1 text-gray-800"
                                onChangeText={(v) => setForm({ ...form, confirmPassword: v })}
                            />
                            <TouchableOpacity onPress={togglePasswordVisibility}>
                                <Ionicons name={isPasswordVisible ? "eye" : "eye-off"} size={20} color="gray" />
                            </TouchableOpacity>
                        </View>

                        {/* Submit Button with Person-Add Icon */}
                        <TouchableOpacity
                            className="bg-[#3bacef] py-4 rounded-2xl shadow-md active:opacity-90 flex-row justify-center items-center"
                            onPress={handleSignup}
                            disabled={isSubmitting}
                        >
                            <Ionicons name="person-add-outline" size={22} color="white" style={{ marginRight: 10 }} />
                            <Text className="text-white text-center font-bold text-lg uppercase">
                                Đăng ký ngay
                            </Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}