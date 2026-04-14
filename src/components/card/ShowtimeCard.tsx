import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

// Định nghĩa kiểu dữ liệu cho Props
interface ShowtimeCardProps {
    title: string;
    ageRating: number;
    version: string;
    poster: string;
    showtimes: string[];
    onPressShowtime?: (time: string) => void;
}

const ShowtimeCard: React.FC<ShowtimeCardProps> = ({
    title,
    ageRating,
    version,
    poster,
    showtimes,
    onPressShowtime
}) => {
    return (
        <View className="flex-row py-4 px-5 border-b border-white/20" style={{ borderBottomWidth: 1.5, borderStyle: 'dashed' }} >
            {/* Movie poster */}
            <Image
                source={{ uri: poster }}
                className="w-32 h-44 rounded-sm bg-gray-800"
                resizeMode="cover"
            />

            {/* Movie info & Showtime */}
            <View className="flex-1 ml-4">
                <Text className="text-white text-lg font-bold mb-3 uppercase" numberOfLines={2}>
                    {title} - T{ageRating} ({version})
                </Text>

                <View className="flex-row flex-wrap gap-2">
                    {showtimes.map((time, index) => (
                        <TouchableOpacity
                            key={index}
                            onPress={() => onPressShowtime?.(time)}
                            activeOpacity={0.7}
                            className="bg-[#272b50] border border-gray-600 px-3 py-2 rounded-md min-w-[70px] items-center"
                        >
                            <Text className="text-gray-200 font-medium">{time}</Text>
                        </TouchableOpacity>
                    ))}
                </View>
            </View>
        </View>
    );
};

export default ShowtimeCard;