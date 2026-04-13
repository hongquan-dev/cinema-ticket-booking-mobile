import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useRouter } from 'expo-router';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { ActivityIndicator, Animated, FlatList, Image, Platform, ScrollView, StatusBar, Text, TouchableOpacity, View, useWindowDimensions } from 'react-native';
import movieApi from '../../src/api/movieApi';
import showtimeApi from '../../src/api/showtimeApi';
import { MovieCard } from '../../src/components/card/MovieCard';

export default function HomePage() {
    const { width } = useWindowDimensions();
    const isWeb = Platform.OS === 'web';
    const router = useRouter();
    const flatListRef = useRef<FlatList>(null);

    // --- PROPORTIONAL CONFIGURATION ---
    const ITEM_SIZE = width * 0.5; // Width of each poster
    const EMPTY_ITEM_SIZE = (width - ITEM_SIZE) / 2; // Padding distance to center the poster
    const POSTER_HEIGHT = ITEM_SIZE * 1.65;
    const AUTO_PLAY_DELAY = 4000;

    const scrollX = useRef(new Animated.Value(0)).current;
    const [activeIndex, setActiveIndex] = useState(0);
    const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);
    const isReversing = useRef(false);

    // Current date and cinema id
    const cinemaId = '78dd3402-6e25-4534-b289-673340392d92';
    const currentDate = ['2026-03-25', '2026-03-26', '2026-03-27', '2026-03-28'];
    const [movies, setMovies] = useState<any[]>([]);
    const [comingSoonMovies, setComingSoonMovies] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);



    useEffect(() => {
        if (movies.length > 0) startAutoPlay();
    }, [movies]);

    // Logic Auto Play (Start auto and Stop auto)
    const startAutoPlay = () => {
        stopAutoPlay();
        timerRef.current = setInterval(() => {
            if (movies.length > 2) {
                const realMoviesCount = movies.length - 2;
                if (realMoviesCount <= 1) return;

                // @ts-ignore
                const currentPos = scrollX._value || 0;
                const currentIndex = Math.round(currentPos / ITEM_SIZE);

                if (currentIndex >= realMoviesCount - 1) {
                    isReversing.current = true;
                } else if (currentIndex <= 0) {
                    isReversing.current = false;
                }

                const nextIndex = isReversing.current
                    ? currentIndex - 1
                    : currentIndex + 1;

                const nextPos = nextIndex * ITEM_SIZE;

                flatListRef.current?.scrollToOffset({
                    offset: Math.max(0, Math.min(nextPos, (realMoviesCount - 1) * ITEM_SIZE)),
                    animated: true,
                });
            }
        }, AUTO_PLAY_DELAY);
    };
    const stopAutoPlay = () => {
        if (timerRef.current) clearInterval(timerRef.current);
    };

    // Call api for fetch Showing Movies 
    const fetchShowingMovies = async () => {
        try {
            const response = await showtimeApi.getGrouped(cinemaId, currentDate[0]);
            await AsyncStorage.setItem('currentDate', JSON.stringify(currentDate));
            await AsyncStorage.setItem('cinemaId', JSON.stringify(cinemaId));

            const mappedMovies = response.data.map((item: any) => ({
                id: item.movie.id,
                roomType: item.roomType,
                title: item.movie.movieName,
                version: item.roomType,
                duration: `${item.movie.duration}p`,
                ageRating: item.movie.ageRating,
                date: item.movie.releaseDate,
                image: { uri: item.movie.posterUrl },
                description: item.movie.description,
                genres: item.movie.selectedGenres,
                directors: item.movie.selectedDirectors,
                actors: item.movie.selectedActors,
            }));

            setMovies([
                { id: 'left-spacer' },
                ...mappedMovies,
                { id: 'right-spacer' }
            ]);
        } catch (error) {
        } finally {
            setLoading(false);
        }
    };

    // Call api for fetch Coming Soon Movies
    const fetchComingSoonMovies = async () => {
        try {
            const response = await movieApi.getComingSoon(currentDate);
            await AsyncStorage.setItem('currentDate', JSON.stringify(currentDate));
            await AsyncStorage.setItem('cinemaId', JSON.stringify(cinemaId));

            const mapped = response.data.map((item: any) => ({
                id: item.id,
                roomType: "Coming Soon",
                title: item.movieName,
                version: item.selectedFormats?.[0] || '2D',
                duration: `${item.duration}p`,
                date: item.releaseDate,
                image: item.posterUrl ? { uri: item.posterUrl } : "",
                ageRating: item.ageRating,
                description: item.description,
                genres: item.selectedGenres,
                directors: item.selectedDirectors,
                actors: item.selectedActors,
            }));
            setComingSoonMovies(mapped);
        } catch (error) {
        }
    };

    useFocusEffect(
        useCallback(() => {
            setActiveIndex(0);
            scrollX.setValue(0);
            isReversing.current = false;

            if (flatListRef.current) {
                flatListRef.current.scrollToOffset({ offset: 0, animated: false });
            }

            const fetchData = async () => {
                await Promise.all([
                    fetchShowingMovies(),
                    fetchComingSoonMovies()
                ]);
                setLoading(false);
            };
            fetchData();
            return () => stopAutoPlay();
        }, [])
    );

    const realMovies = movies.filter(m => m.image);
    const currentMovie = realMovies[activeIndex] || null;

    // Calculate the current index when scrolling to update the Text below
    const onScroll = Animated.event(
        [{ nativeEvent: { contentOffset: { x: scrollX } } }],
        {
            useNativeDriver: !isWeb,
            listener: (event: any) => {
                const offsetX = event.nativeEvent.contentOffset.x;
                const index = Math.round(offsetX / ITEM_SIZE);

                if (index >= 0 && index < realMovies.length) {
                    setActiveIndex(index);
                }
            },
        }
    );

    // Help function for navigate to Movie Details
    const handleMoviePress = (movie: any, isBooking: string) => {
        if (!movie) return;

        router.push({
            pathname: "/(screens)/moviedetails",
            params: {
                id: movie.id,
                roomType: movie.roomType,
                title: movie.title,
                poster: movie.image?.uri,
                version: movie.version,
                duration: movie.duration,
                ageRating: `T${movie.ageRating}`,
                releaseDate: new Date(movie.date).toLocaleDateString('vi-VN'),
                description: movie.description,
                genres: movie.genres,
                directors: movie.directors,
                actors: movie.actors,
                isBooking: isBooking
            }
        });
    };

    if (loading) {
        return (
            <View className="flex-1 bg-[#272b50] justify-center items-center">
                <ActivityIndicator size="large" color="#1e90ff" />
            </View>
        );
    }
    return (
        <ScrollView
            className="flex-1 bg-[#272b50]"
            contentContainerStyle={{ paddingBottom: 5 }}
            showsVerticalScrollIndicator={false}
        >
            <StatusBar barStyle="light-content" />
            {/* 1. Header Logo & Title */}
            <View className="items-center mt-20 mb-8 px-4">
                <View className="flex-row items-center justify-center">
                    <View className="w-16 h-14 rounded-lg overflow-hidden bg-white/10">
                        <Image
                            source={require('../../assets/images/NCC_Logo.jpg')}
                            style={{ width: '100%', height: '100%' }}
                            resizeMode="cover"
                        />
                    </View>
                    <View className="ml-4">
                        <Text className="text-white text-[16px] font-bold uppercase text-center">
                            Trung tâm chiếu phim quốc gia
                        </Text>
                        <Text className="text-white text-[14px] font-medium uppercase text-center">
                            National Cinema Center
                        </Text>
                    </View>
                </View>
            </View>

            {/* Movie are showing */}
            <View className="flex-row items-center px-5 mb-6">
                <View className="w-4 h-4 rounded-full bg-red-600 mr-2" />
                <Text className="text-white text-[20px] font-semibold">Phim đang chiếu</Text>
            </View>

            {/* 2. Movie carousel */}
            {realMovies.length > 0 ? (
                <View>
                    <Animated.FlatList
                        ref={flatListRef}
                        data={movies}
                        horizontal
                        showsHorizontalScrollIndicator={false}
                        keyExtractor={(item, index) => `${item.id}-${index}`}
                        snapToInterval={ITEM_SIZE} // Stop point for each item
                        snapToAlignment="start"    // Alignment point for stopping
                        decelerationRate={Platform.OS === 'ios' ? 'fast' : 0.8}
                        scrollEventThrottle={16}
                        disableIntervalMomentum={true} // Only allow sliding one by one
                        onScroll={onScroll}
                        onScrollBeginDrag={stopAutoPlay} // Stop auto play when user touch
                        onScrollEndDrag={startAutoPlay} // Start auto play when user stop touch
                        contentContainerStyle={{ paddingHorizontal: 0 }}
                        renderItem={({ item, index }) => {
                            // If it's a dummy item
                            if (!item.image) {
                                return <View style={{ width: EMPTY_ITEM_SIZE }} />;
                            }

                            // Zoom effect when in the middle
                            const inputRange = [(index - 2) * ITEM_SIZE, (index - 1) * ITEM_SIZE, index * ITEM_SIZE];
                            const scale = scrollX.interpolate({ inputRange, outputRange: [0.8, 1, 0.8], extrapolate: 'clamp', });
                            const opacity = scrollX.interpolate({ inputRange, outputRange: [0.6, 1, 0.6], extrapolate: 'clamp', });

                            return (
                                <View style={{ width: ITEM_SIZE }}>
                                    <TouchableOpacity
                                        activeOpacity={0.9}
                                        onPress={() => handleMoviePress(item, 'true')}
                                        style={{ width: ITEM_SIZE }}
                                    >
                                        <Animated.View style={{ transform: [{ scale }], opacity, alignItems: 'center', }}>
                                            <Image source={item.image}
                                                style={{ width: ITEM_SIZE, height: POSTER_HEIGHT, borderRadius: 15, backgroundColor: '#1a1d3d' }} />
                                        </Animated.View>
                                    </TouchableOpacity>
                                </View>
                            );
                        }}
                    />
                </View>
            ) : (
                <View className="px-5 py-10 items-center justify-center">
                    <Text className="text-gray-400 text-lg">Chưa có phim đang chiếu.</Text>
                </View>
            )}

            {/* 3. Information Movie & Booking Button */}
            {currentMovie && (
                <View className="px-6 mt-10 flex-row justify-between items-center">
                    <View className="flex-1 mr-4 gap-2">
                        <Text className="text-white text-2xl font-semibold">
                            {currentMovie.title} - T{currentMovie.ageRating}
                        </Text>
                        <View className="flex-row items-center">
                            <View className="border border-[#F472B6] rounded-md px-2 py-0.5">
                                <Text className="text-[#F472B6] font-bold text-lg">
                                    {currentMovie.version}
                                </Text>
                            </View>
                        </View>
                        <Text className="text-gray-400 text-lg">
                            {currentMovie.duration} - {new Date(currentMovie.date).toLocaleDateString('vi-VN')}
                        </Text>
                    </View>

                    <TouchableOpacity
                        activeOpacity={0.8}
                        className="bg-[#1e90ff] px-8 py-4 rounded-xl"
                        onPress={() => handleMoviePress(currentMovie, 'true')}
                    >
                        <Text className="text-white font-bold text-[18px]">ĐẶT VÉ</Text>
                    </TouchableOpacity>
                </View>
            )}

            <View className="h-[1px] bg-gray-500/30 mx-6 my-6" />

            {/* Movie are coming soon */}
            <View className="flex-row items-center px-5 mb-6">
                <View className="w-4 h-4 rounded-full bg-red-600 mr-2" />
                <Text className="text-white text-[20px] font-semibold">Phim sắp chiếu</Text>
            </View>

            {/* 4. Section movie are comming soon */}
            {comingSoonMovies.length > 0 ? (
                <View className="px-5">
                    <FlatList
                        data={comingSoonMovies}
                        horizontal
                        showsHorizontalScrollIndicator={false}
                        keyExtractor={(item) => item.id}
                        contentContainerStyle={{ gap: 18 }}
                        renderItem={({ item }) => (
                            <MovieCard
                                title={item.title}
                                imageSource={item.image}
                                version={item.version}
                                duration={item.duration}
                                date={item.date}
                                ageRating={item.ageRating}
                                width={width * 0.4}
                                onPress={() => handleMoviePress(item, 'false')}
                            />
                        )}
                    />
                </View>) :
                (<View className="px-5 py-10 items-center justify-center">
                    <Text className="text-gray-400 text-lg">Chưa có phim sắp chiếu.</Text>
                </View>)}
        </ScrollView>
    );
}