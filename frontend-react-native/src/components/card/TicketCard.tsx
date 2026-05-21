import { CalendarDays, Clock, MapPin, Ticket } from 'lucide-react-native';
import React, { useState } from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';
import { getSeatTypeInfo } from '../../enums/seatTypeEnums';

export interface TicketCardProps {
    item: any;
    onCancel?: () => void;
}

// Age Rating Badge helper
export const getAgeRatingBadge = (rating: number) => {
    if (rating === 0) return { text: 'P', color: 'bg-green-600', textColor: 'text-white' };
    if (rating === 6 || rating < 13) return { text: `T6`, color: 'bg-blue-500', textColor: 'text-white' };
    if (rating >= 13 && rating < 16) return { text: `T13`, color: 'bg-yellow-600', textColor: 'text-white' };
    if (rating >= 16 && rating < 18) return { text: `T16`, color: 'bg-orange-600', textColor: 'text-white' };
    return { text: `T18`, color: 'bg-red-600', textColor: 'text-white' };
};

// Date/time formatting helper
export const formatShowtime = (dateString: string) => {
    if (!dateString) return { time: '', date: '' };
    try {
        const date = new Date(dateString);
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();

        const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
        const dayOfWeek = days[date.getDay()];

        return {
            time: `${hours}:${minutes}`,
            date: `${dayOfWeek}, ${day}/${month}/${year}`
        };
    } catch (e) {
        return { time: '', date: '' };
    }
};

// Movie Poster component with fallback
export const MoviePoster = ({ uri }: { uri?: string }) => {
    const [hasError, setHasError] = useState(false);

    if (!uri || hasError) {
        return (
            <View className="w-32 h-44 bg-[#1e2240] rounded-xl items-center justify-center border border-white/10 shadow-inner">
                <Ticket size={28} color="#8E8E93" />
                <Text className="text-gray-500 text-[10px] mt-1 text-center font-medium">NCC Cinema</Text>
            </View>
        );
    }

    return (
        <Image
            source={{ uri }}
            className="w-32 h-44 rounded-xl bg-[#1a1d3d]"
            resizeMode="cover"
            onError={() => setHasError(true)}
        />
    );
};

export const TicketCard: React.FC<TicketCardProps> = ({ item, onCancel }) => {
    const { time, date } = formatShowtime(item.startTime);
    const isCancelled = item.status === 'CANCELLED';
    const age = getAgeRatingBadge(item.ageRating);
    const seatTypeInfo = getSeatTypeInfo(item.seat?.seatType);

    return (
        <View className="mx-5 mb-5 bg-[#1e2240] rounded-2xl border border-white/5 shadow-lg relative overflow-hidden">
            {/* Visual Notch Left (mimics cut ticket) */}
            <View
                style={{
                    position: 'absolute',
                    left: -10,
                    top: '63%',
                    width: 20,
                    height: 20,
                    borderRadius: 10,
                    backgroundColor: '#272b50', // Match screen background
                    zIndex: 10,
                }}
            />

            {/* Visual Notch Right */}
            <View
                style={{
                    position: 'absolute',
                    right: -10,
                    top: '63%',
                    width: 20,
                    height: 20,
                    borderRadius: 10,
                    backgroundColor: '#272b50',
                    zIndex: 10,
                }}
            />

            {/* UPPER PART - Movie and Theater Information */}
            <View className="flex-row p-4 pb-5 items-start">
                <MoviePoster uri={item.posterUrl} />

                <View className="flex-1 ml-4">
                    <Text className="text-white text-[17px] font-bold leading-tight mb-1" numberOfLines={2}>
                        {item.movieName}
                    </Text>

                    {/* Badges row */}
                    <View className="flex-row items-center mb-3">
                        <View className={`${age.color} px-1.5 py-1 rounded mr-2`}>
                            <Text className={`${age.textColor} text-[11px] font-bold`}>{age.text}</Text>
                        </View>
                        <View className="border border-pink-500/50 px-1.5 py-1 rounded">
                            <Text className="text-pink-500 text-[11px] font-bold">{item.format}</Text>
                        </View>
                        <Text className="text-gray-400 text-[11px] ml-3 font-bold">{item.duration} Phút</Text>
                    </View>

                    {/* Showtime info */}
                    <View className="flex-row items-center gap-x-1.5 mb-0.5">
                        <CalendarDays size={15} color="#8E8E93" />
                        <Text className="text-gray-300 text-sm font-medium">{date}</Text>
                    </View>

                    <View className="flex-row items-center gap-x-1.5 mb-0.5">
                        <Clock size={15} color="#8E8E93" />
                        <Text className="text-[#1e90ff] text-sm font-bold">{time}</Text>
                    </View>

                    {/* Cinema info */}
                    <View className="flex-row items-center gap-x-1.5">
                        <MapPin size={15} color="#8E8E93" />
                        <Text className="text-gray-300 text-sm font-medium" numberOfLines={1}>
                            {item.roomName}
                        </Text>
                    </View>
                </View>
            </View>

            {/* DASHED LINE SEPARATOR (Dynamic fallback using multiple Views for cross-platform compatibility) */}
            <View className="flex-row items-center justify-between absolute left-4 right-4 top-[63%] mt-[5px]">
                {Array.from({ length: 32 }).map((_, i) => (
                    <View key={i} className="w-[4px] h-[1px] bg-white/10" />
                ))}
            </View>

            {/* LOWER PART - Ticket status, seat codes and price */}
            <View className="px-4 pt-3 pb-3 flex-row items-center justify-between bg-black/10">
                <View className="gap-y-1">
                    <Text className="text-gray-400 text-[10px] font-bold uppercase tracking-wider">Mã vé / Ghế</Text>
                    <Text className="text-white text-[14px] font-medium">
                        <Text className="text-gray-300 font-bold">{item.orderCode}</Text> • <Text className={`${seatTypeInfo.textColor} font-bold`}>{item.seat?.seatNumber}</Text>
                        <Text className="text-gray-300"> </Text>
                        <Text className={`${seatTypeInfo.textColor} font-bold`}>({seatTypeInfo.label})</Text>
                        <Text className="text-gray-300"></Text>
                    </Text>
                </View>

                <View className="items-end gap-y-1">
                    <Text className="text-gray-400 text-[10px] font-bold uppercase tracking-wider">Thanh toán</Text>
                    <Text className="text-emerald-400 text-[14px] font-black">
                        {item.totalPrice.toLocaleString()}đ
                    </Text>
                </View>
            </View>

            {/* BOTTOM ACTION BAR */}
            <View className={isCancelled ? 'px-4 py-3.5' : 'pl-4 pr-3 py-2 bg-black/20 flex-row justify-between items-center border-t border-white/5'}>
                {/* Status Badge */}
                <View className="flex-row items-center">
                    <View className={`w-3 h-3 rounded-full mr-2 ${isCancelled ? 'bg-red-500' : 'bg-emerald-500'}`} />
                    <Text className={`text-md font-semibold ${isCancelled ? 'text-red-400' : 'text-emerald-400'}`}>
                        {isCancelled ? 'Đã hủy vé' : 'Đã xác nhận'}
                    </Text>
                </View>

                {/* QR Code Action Button */}
                {!isCancelled && (
                    <TouchableOpacity
                        activeOpacity={0.8}
                        onPress={onCancel}
                        className="bg-red-500/10 border border-red-500/30 flex-row items-center px-3 py-1.5 rounded-lg"
                    >
                        <Text className="text-red-500 text-[13px] font-bold">Hủy vé</Text>
                    </TouchableOpacity>
                )}
            </View>
        </View>
    );
};

export default TicketCard;
