import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, FlatList, StatusBar, Text, TouchableOpacity, View } from 'react-native';
import { PostCard } from '../../src/components/card/PostCard';
import { PostCategory, PostStatus } from '../../src/enums/postEnums';
import postApi from '../../src/services/postApi';

export default function NotificationsPage() {
    const router = useRouter();
    const [posts, setPosts] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);
    const [refreshing, setRefreshing] = useState(false);

    const loadNews = async (isPullToRefresh = false) => {
        if (isPullToRefresh) {
            setRefreshing(true);
        } else {
            setLoading(true);
        }

        try {
            // API parameters to filter by PUBLISHED status
            const params = {
                page: 1,
                size: 20,
                status: PostStatus.PUBLISHED,
                category: PostCategory.ANNOUNCEMENT
            };

            const response: any = await postApi.getAll(params);

            // Map the API response to the local state
            setPosts(response.posts || []);
        } catch (error) {
            console.error("Failed to fetch news:", error);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadNews();
            return () => { };
        }, [])
    );

    const formatTime = (isoString: string) => {
        if (!isoString) return '';
        const date = new Date(isoString);
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();

        return `${hours}:${minutes} ${day}/${month}/${year}`;
    };

    return (
        <View className="flex-1 bg-[#272b50]">
            <StatusBar barStyle="light-content" />

            <View className="flex-row items-center justify-between px-[15px] pt-[50px] z-10 bg-[#272b50]">
                <TouchableOpacity onPress={() => router.back()} className="p-[5px]">
                    <Ionicons name="arrow-back" size={28} color="white" />
                </TouchableOpacity>
                <Text className="text-white text-[18px] font-medium">Thông báo</Text>
                <View className="w-[28px]" />
            </View>

            {/* Show full screen loader only on initial load */}
            {loading && !refreshing ? (
                <View className="flex-1 justify-center items-center">
                    <ActivityIndicator size="large" color="#1e90ff" />
                </View>
            ) : (
                <FlatList
                    data={posts}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <PostCard
                            title={item.title}
                            time={formatTime(item.createdAt)}
                            imageUri={item.thumbnailUrl} // Ensure this field matches your Post model
                            onPress={() => router.push({
                                pathname: '/(account)/notificationdetails',
                                params: {
                                    id: item.id,
                                    category: item.category,
                                    title: item.title,
                                    description: item.description,
                                    content: item.content,
                                    thumbnailUrl: item.thumbnailUrl,
                                    createAt: item.createdAt,
                                }
                            })}
                        />
                    )}
                    contentContainerStyle={{ paddingBottom: 40 }}
                    showsVerticalScrollIndicator={false}

                    // Pull to refresh logic
                    onRefresh={() => loadNews(true)}
                    refreshing={refreshing}

                    // UI when no data is returned
                    ListEmptyComponent={
                        <View className="mt-20 items-center">
                            <Text className="text-gray-400 text-lg">No updates at the moment</Text>
                        </View>
                    }
                />
            )}
        </View>
    );
}