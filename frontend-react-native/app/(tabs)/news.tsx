import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useState } from 'react';
import { ActivityIndicator, FlatList, StatusBar, Text, View } from 'react-native';
import { PostCard } from '../../src/components/card/PostCard';
import { PostCategory, PostStatus } from '../../src/enums/postEnums';
import postApi from '../../src/services/postApi';

export default function NewsPage() {
    const router = useRouter();
    const [posts, setPosts] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    const loadNews = async () => {
        try {
            setLoading(true);
            // API parameters to filter by PUBLISHED status
            const params = {
                page: 1,
                size: 20,
                status: PostStatus.PUBLISHED,
                category: PostCategory.NEWS
            };

            const response: any = await postApi.getAll(params);

            // Map the API response to the local state
            setPosts(response.posts || []);
        } catch (error) {
            // console.error("Failed to fetch news:", error);
        } finally {
            setLoading(false);
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
        <View className="flex-1 bg-[#272b50] pt-14 px-4">
            <StatusBar barStyle="light-content" />

            {/* Show full screen loader only on initial load */}
            {loading ? (
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
                            imageUri={item.thumbnailUrl}
                            onPress={() => router.push({
                                pathname: '/(details)/newsdetails',
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

                    // UI when no data is returned
                    ListEmptyComponent={
                        <View className="mt-16 items-center px-8">
                            <Ionicons name="newspaper-outline" size={50} color="#8E8E93" />
                            <Text className="text-white text-xl font-bold mt-3">
                                Hiện không có tin tức nào
                            </Text>
                            <Text className="text-gray-400 text-[15px] text-center mt-1 leading-relaxed">
                                Các tin tức, sự kiện mới nhất và bài viết cập nhật từ hệ thống sẽ xuất hiện tại đây.
                            </Text>
                        </View>
                    }
                />
            )}
        </View>
    );
}