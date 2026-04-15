import useNotification from '@/src/hooks/useNotification';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as ImagePicker from 'expo-image-picker';
import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, Image, ScrollView, StatusBar, Text, TextInput, TouchableOpacity, View } from 'react-native';
import userApi from '../../src/services/userApi';

const EditProfile = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const [userId, setUserId] = useState<string>('');

    const [username, setUsername] = useState('');
    const [fullName, setFullName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [email, setEmail] = useState('');
    const [address, setAddress] = useState('');
    const [avatar, setAvatar] = useState('');
    const [verified, setVerified] = useState(false);
    const [createdAt, setCreatedAt] = useState('');

    const [fetching, setFetching] = useState(true);
    const [loading, setLoading] = useState(false);
    const [validate, setValidate] = useState(false);
    const { notify, NotificationComponent } = useNotification();

    const isFormValid = fullName.trim() !== '' &&
        phoneNumber.trim() !== '' &&
        email.trim() !== '' &&
        address.trim() !== '';

    useFocusEffect(
        useCallback(() => {
            const fetchUserData = async () => {
                try {
                    const storeUserId = await AsyncStorage.getItem('user_id');

                    if (storeUserId) {
                        setUserId(storeUserId);

                        const response: any = await userApi.getById(storeUserId);

                        if (response && response.data) {
                            const data = response.data;
                            setUsername(data.username || '');
                            setFullName(data.fullName || '');
                            setPhoneNumber(data.phoneNumber || '');
                            setEmail(data.email || '');
                            setAddress(data.address || '');
                            setAvatar(data.avatar || '');
                            setVerified(data.verified || false);

                            if (data.createdAt) {
                                const date = new Date(data.createdAt);
                                const formattedDate = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
                                setCreatedAt(formattedDate);
                            }
                        }
                    } else {
                        // console.warn("No user_id found in storage");
                    }
                } catch (error) {
                    // console.error("Failed to fetch user data:", error);
                } finally {
                    setFetching(false);
                }
            };

            fetchUserData();
        }, [])
    );

    const handleSave = async () => {
        setLoading(true);
        try {
            // Phone validation (Must be exactly 10 digits)
            const phoneRegex = /^\d{10}$/;
            if (!phoneRegex.test(phoneNumber)) {
                notify("Số điện thoại lỗi", "Số điện thoại phải có đúng 10 chữ số và chỉ chứa số", "warning");
                return;
            }

            // Email validation (Strictly @gmail.com)
            const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
            if (!gmailRegex.test(email)) {
                notify("Email không hợp lệ", "Email phải có định dạng @gmail.com", "warning");
                return;
            }

            const updateData = {
                fullName: fullName ? fullName.trim() : null,
                phoneNumber,
                address,
                email
            };

            const response: any = await userApi.update(userId, updateData);

            await notify("Thành công", "Cập nhật thông tin thành công!", "success");
        } catch (error: any) {
            if (error.toString() === "Email already exists in the system") {
                await notify("Lỗi", "Email đã có người sử dụng", "error");
            }
            else if (error.toString() === "Phone number already exists in the system") {
                await notify("Lỗi", "Số điện thoại đã có người sử dụng", "error");
            }
            else {
                await notify("Lỗi", "Cập nhật thất bại", "error");
            }
        } finally {
            setLoading(false);
        }
    };

    const handleEditAvatar = async () => {
        // Request permission to access image library
        const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();

        if (status !== 'granted') {
            await notify("Quyền truy cập", "Bạn cần cấp quyền truy cập thư viện ảnh để thực hiện tính năng này", "warning");
            return;
        }

        // Open image library
        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images,
            allowsEditing: true, // Allow crop image
            aspect: [1, 1],      // Square aspect ratio for avatar
            quality: 0.8,        // Compress image slightly for faster upload
        });

        if (!result.canceled) {
            const selectedImageUri = result.assets[0].uri;

            // Upload image
            await uploadAvatar(selectedImageUri);
        }
    };

    const uploadAvatar = async (uri: string) => {
        setLoading(true);
        try {
            const formData = new FormData();

            // Create file for FormData
            const filename = uri.split('/').pop();
            const match = /\.(\w+)$/.exec(filename || '');
            const type = match ? `image/${match[1]}` : `image`;

            // @ts-ignore
            formData.append('file', {
                uri: uri,
                name: filename || 'avatar.jpg',
                type: type,
            });

            // Call API updateAvatar
            const response: any = await userApi.updateAvatar(userId, formData);

            // Update UI with new avatar link from Cloudinary
            if (response && response.data) {
                setAvatar(response.data.avatar);
                await notify("Thành công", "Cập nhật ảnh đại diện thành công!", "success");
            }

        } catch (error: any) {
            await notify("Lỗi", "Không thể tải ảnh lên server", "error");
        } finally {
            setLoading(false);
        }
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
                <Text className="text-white text-[18px] font-medium">Thông tin tài khoản</Text>
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
                            {loading && (
                                <View className="absolute inset-0 z-20 bg-black/40 rounded-full items-center justify-center">
                                    <ActivityIndicator color="white" />
                                </View>
                            )}

                            <Image
                                source={avatar ? { uri: avatar } : require('../../assets/default_avatar.jpg')}
                                className="w-full h-full rounded-full border-4 border-gray-600"
                                resizeMode="cover"
                            />

                            <TouchableOpacity
                                className="absolute bottom-0 right-[5px] bg-[#3bacef] w-11 h-11 rounded-full z-30 items-center justify-center shadow-lg"
                                activeOpacity={0.8}
                                onPress={handleEditAvatar}
                                disabled={loading}
                            >
                                <Ionicons name="camera" size={24} color="white" />
                            </TouchableOpacity>
                        </View>
                    </View>

                    {/* Username Row (ReadOnly) */}
                    <InputLabel label="Tên đăng nhập" />
                    <View className="bg-gray-400/50 rounded-xl px-5 py-3">
                        <Text className="text-gray-300 text-lg">{username}</Text>
                    </View>

                    {/* Full Name Row */}
                    <InputLabel label="Tên đầy đủ" />
                    <TextInput
                        className="bg-gray-50 border border-gray-100 px-5 py-4 rounded-xl w-full text-gray-800"
                        value={fullName}
                        onChangeText={setFullName}
                        placeholder="Tên đầy đủ"
                    />

                    {/* Phone Number */}
                    <InputLabel label="Số điện thoại" />
                    <TextInput
                        className="bg-white rounded-xl px-5 py-4"
                        value={phoneNumber}
                        onChangeText={setPhoneNumber}
                        keyboardType="phone-pad"
                        placeholder="Số điện thoại"
                    />

                    {/* Email */}
                    <InputLabel label="Email" />
                    <TextInput
                        className="bg-white rounded-xl px-5 py-4"
                        value={email}
                        onChangeText={setEmail}
                        keyboardType="email-address"
                        placeholder="Email"
                    />

                    {/* Address */}
                    <InputLabel label="Địa chỉ" />
                    <TextInput
                        className="bg-white rounded-xl px-5 py-4"
                        value={address}
                        onChangeText={setAddress}
                        placeholder="Địa chỉ"
                    />

                    {/* Verify Status & Join Date */}
                    <View className="flex-row justify-between items-center mt-6 bg-gray-800 rounded-xl px-5 py-4 border border-gray-600">
                        <View className="flex-row items-center">
                            <View className={`w-4 h-4 rounded-full mr-2 ${verified ? 'bg-green-500' : 'bg-red-500'}`} />
                            <Text className={`font-bold text-lg ${verified ? 'text-green-500' : 'text-red-500'}`}>
                                {verified ? 'Đã xác thực' : 'Chưa xác thực'}
                            </Text>
                        </View>
                        <View className="flex-row items-center">
                            <Text className="text-white font-medium text-lg">{createdAt}</Text>
                        </View>
                    </View>

                    <View className="h-20" />
                </ScrollView>
            )}

            {/* Bottom Save Button */}
            {!fetching && (
                <View className="px-5 pb-6 bg-[#272b50]">
                    <TouchableOpacity
                        disabled={!isFormValid || loading}
                        onPress={handleSave}
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

export default EditProfile;