import useNotification from '@/src/hooks/useNotification';
import { Ionicons } from '@expo/vector-icons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React from 'react';
import { Animated, Dimensions, ScrollView, StatusBar, Text, TouchableOpacity, View } from 'react-native';

const MemberCard = () => {
    const router = useRouter();
    const params = useLocalSearchParams();
    const { notify, NotificationComponent } = useNotification();

    const { width, height } = Dimensions.get('window');
    const POSTER_HEIGHT = height * 0.5;

    const handleClick = async () => {
        await notify("Thông báo", "Hiện tính năng này chưa được hỗ trợ", "info");
    }

    return (
        <View className="flex-1 bg-[#1a1d3d]">
            <StatusBar barStyle="light-content" />
            {NotificationComponent}

            {/* 1. Background Poster (Fixed) */}
            <Animated.Image
                source={require('../../assets/member_card_img.jpg')}
                style={{
                    position: 'absolute',
                    top: 0,
                    width: width,
                    height: POSTER_HEIGHT,
                    backgroundColor: 'white',
                }}
                resizeMode="cover"
            />

            {/* 2. Custom Header */}
            <View className="flex-row items-center justify-between px-[15px] pt-[50px] pb-[13px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Thẻ thành viên U22</Text>
                <View className="w-[28px]" />
            </View>

            {/* 3. Main Container */}
            <View className="flex-1">
                {/* Spacer */}
                <View style={{ height: POSTER_HEIGHT * 0.45 }} />

                {/* Movie Detail Container */}
                <View className="flex-1 bg-[#272b50] rounded-t-[0px] px-[20px] pt-6 shadow-black shadow-offset-[0px/-10px] shadow-opacity-30 shadow-radius-10 elevation-20">

                    <View className="flex-row justify-between items-start mb-5">
                        <View className="flex-1">
                            <Text className="text-white text-[20px] font-semibold text-center">
                                Thẻ thành viên U22
                            </Text>
                        </View>
                    </View>

                    <View className="h-[1px] bg-gray-500/30 w-full mb-4" />

                    <ScrollView
                        className="flex-1"
                        showsVerticalScrollIndicator={false}
                        overScrollMode="never"
                    >
                        <Text className="text-white text-[16px] font-bold mb-4">
                            🎬 THẺ U22 – THẺ CỦA TỤI MÌNH!
                        </Text>

                        <Text className="text-gray-300 text-[16px] leading-7 mb-6">
                            Bạn 22 tuổi trở xuống? Bạn mê phim? Vậy thì thẻ U22 của Trung tâm Chiếu phim Quốc gia (NCC) là dành cho bạn rồi đó!
                        </Text>

                        <Text className="text-white text-[17px] font-bold mb-4">
                            1. 💥 Có gì "xịn" trong thẻ U22?
                        </Text>

                        <View className="mb-4">
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Dành riêng cho tụi mình: Học sinh, sinh viên, hoặc người đang dưới 22 tuổi đều đăng ký được.
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Cá nhân hóa cực chill: Thẻ gắn với thông tin của bạn, chỉ cần CMND/CCCD hoặc thẻ học sinh – sinh viên là xong.
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Thiết kế trẻ – chất – đậm chất điện ảnh 🎞
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Thẻ đăng ký mới là thẻ phi vật lý được tích hợp trên App/Web của NCC
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Đăng ký siêu dễ: Có thể đăng ký trực tiếp tại rạp hoặc online luôn nha!
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7">
                                • Tham gia hội yêu phim: Có thẻ là có "vé" để hòa mình vào các hoạt động văn hóa – giải trí chất lượng cùng cộng đồng mê phim cực cool.
                            </Text>
                        </View>

                        <Text className="text-white text-[17px] font-bold mb-4">
                            2. 🎁 Thành viên U22 được gì?
                        </Text>

                        <View className="mb-4">
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                ƯU ĐÃI GIÁ VÉ SIÊU HỜI!
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                🎟️ Chỉ 55.000đ/vé 2D
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                👉 Áp dụng từ Thứ 2 đến Thứ 6
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                👉 Mỗi ngày mua được 1 vé giá ưu đãi
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                👉 Chỉ áp dụng mua trực tiếp tại quầy, không dùng cho ghế đôi
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-6">
                                👉 Nhớ mang theo thẻ U22 (bản cứng hoặc bản điện tử trên thiết bị di động) khi mua vé nha!
                            </Text>

                            <View className="h-[1px] bg-white w-12 mb-6" />

                            <Text className="text-white text-[17px] font-bold mb-4">
                                3. 📝 Cách đăng ký thành viên U22 cực dễ
                            </Text>

                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                🔹 Đăng ký online:
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Vào trang web/app Trung tâm đăng nhập hoặc đăng ký tài khoản mới
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Vào phần thẻ thành viên để đăng ký thẻ thành viên U22
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Sau đó đến quầy vé hoặc quầy thông tin để xác nhận và kích hoạt thẻ (nhớ mang theo CCCD hoặc thẻ HSSV còn hiệu lực nha)
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-6">
                                • Nhận và sử dụng thẻ ngay lập tức!
                            </Text>

                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                🔹 Đăng ký trực tiếp tại rạp:
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Mang theo CCCD hoặc thẻ HSSV còn hiệu lực
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Đến quầy vé/quầy thông tin đăng ký là xong
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-6">
                                • Nhận và sử dụng thẻ ngay lập tức!
                            </Text>

                            <View className="h-[1px] bg-white w-12 mb-6" />

                            <Text className="text-white text-[17px] font-bold mb-4">
                                4. ✅ Điều kiện làm thẻ U22
                            </Text>

                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Nếu bạn là sinh viên/học sinh chưa có thẻ HSSV, dùng CCCD thay thế
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-6">
                                • Nếu bạn chưa có thẻ học sinh/CCCD, phải có mặt tại rạp để xác nhận thông tin (dành cho người dưới 14 tuổi)
                            </Text>

                            <View className="h-[1px] bg-white w-12 mb-6" />

                            <Text className="text-white text-[17px] font-bold mb-4">
                                ⚠️ Lưu ý nè!
                            </Text>

                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Các mem sở hữu thẻ U22 mới được hưởng ưu đãi
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                • Không áp dụng vào các ngày:
                            </Text>
                            <Text className="text-gray-300 text-[16px] leading-7 mb-4">
                                🎉 20/10, 20/11, 31/10 (Halloween), các ngày Lễ, Tết, suất chiếu sớm và suất chiếu đặc biệt
                            </Text>
                        </View>
                    </ScrollView>

                    <View className="p-4 bg-[#272b50] border-t border-white/5 mb-3">
                        <TouchableOpacity
                            className={`py-4 rounded-2xl items-center bg-[#1e90ff]`}
                            activeOpacity={0.8}
                            onPress={handleClick}
                        >
                            <Text className="text-white font-bold text-lg uppercase">
                                Đăng kí thẻ thành viên U22
                            </Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </View>
    );
};

export default MemberCard;