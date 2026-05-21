import useNotification from '@/src/hooks/useNotification';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect, useRouter } from 'expo-router';
import { Ticket } from 'lucide-react-native';
import React, { useCallback, useMemo, useState } from 'react';
import {
    ActivityIndicator,
    FlatList,
    StatusBar,
    Text,
    TouchableOpacity,
    View
} from 'react-native';
import TicketCard from '../../src/components/card/TicketCard';
import useConfirm from '../../src/hooks/useConfirm';
import bookingApi from '../../src/services/bookingApi';

// --- 1. HELPERS ---
const processTickets = (ticketsList: any[]) => {
    if (!ticketsList || ticketsList.length === 0) return [];

    return ticketsList.map((ticket, idx) => ({
        id: ticket.id || `${ticket.orderCode}-${ticket.seat?.seatNumber || idx}`,
        orderCode: ticket.orderCode,
        movieName: ticket.showtime?.movie?.movieName || 'Phim',
        posterUrl: ticket.showtime?.movie?.posterUrl,
        ageRating: ticket.showtime?.movie?.ageRating || 0,
        duration: ticket.showtime?.movie?.duration || 0,
        format: ticket.showtime?.format || ticket.showtime?.room?.roomType || '2D',
        cinemaName: ticket.showtime?.room?.cinema?.name || 'Rạp Chiếu Phim',
        cinemaAddress: ticket.showtime?.room?.cinema?.address || '',
        roomName: ticket.showtime?.room?.name || 'Phòng chiếu',
        startTime: ticket.showtime?.startTime,
        createAt: ticket.createAt || ticket.createdAt,
        status: ticket.status || 'BOOKED',
        seat: ticket.seat || null,
        totalPrice: ticket.finalPrice || 0,
        showtime: ticket.showtime
    })).sort((a: any, b: any) => {
        const dateA = new Date(a.createAt).getTime();
        const dateB = new Date(b.createAt).getTime();
        return dateB - dateA;
    });
};

export default function TicketsPage() {
    const router = useRouter();
    const [userId, setUserId] = useState<string | null>(null);
    const [tickets, setTickets] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [activeTab, setActiveTab] = useState<'ALL' | 'BOOKED' | 'CANCELLED'>('ALL');

    const { confirm, ConfirmComponent } = useConfirm();
    const { notify, NotificationComponent } = useNotification();

    // Fetch tickets logic
    const loadTickets = async (isPull = false) => {
        if (isPull) {
            setRefreshing(true);
        } else {
            setLoading(true);
        }

        try {
            const currentUserId = await AsyncStorage.getItem('user_id');
            setUserId(currentUserId);

            if (currentUserId) {
                const response: any = await bookingApi.getUserTickets(currentUserId);
                if (response && response.tickets && response.tickets.length > 0) {
                    const processed = processTickets(response.tickets);
                    setTickets(processed);
                } else if (response && Array.isArray(response) && response.length > 0) {
                    const processed = processTickets(response);
                    setTickets(processed);
                } else {
                    setTickets([]);
                }
            } else {
                setTickets([]);
            }
        } catch (error) {
            console.log("Failed to fetch tickets", error);
            setTickets([]);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadTickets();
            return () => { };
        }, [])
    );

    // Handle cancel ticket
    const handleCancelTicket = async (ticketId: string) => {
        const isConfirmed = await confirm("Hủy vé", "Nếu bạn hủy vé này, những vé có mã " + ticketId + " cũng sẽ bị hủy theo.\nKhông thể hoàn tác!", "danger");
        if (!isConfirmed) return;

        try {
            setLoading(true);
            await bookingApi.cancelTicket(ticketId);
            notify("Hủy vé thành công", "Vé của bạn đã được hủy.", "success");
            loadTickets();
        } catch (error) {
            console.log("Failed to cancel ticket", error);
            notify("Hủy vé thất bại", "Đã có lỗi xảy ra khi hủy vé. Vui lòng thử lại sau.", "error");
        } finally {
            setLoading(false);
        }
    };

    const filteredTickets = useMemo(() => {
        if (activeTab === 'ALL') return tickets;
        return tickets.filter(ticket => ticket.status === activeTab);
    }, [tickets, activeTab]);

    return (
        <View className="flex-1 bg-[#272b50] pt-14">
            <StatusBar barStyle="light-content" />
            {ConfirmComponent}
            {NotificationComponent}

            {/* --- STATE FILTERS (TABS) --- */}
            <View className="flex-row mx-5 mb-2 bg-[#1a1d3d] p-1 rounded-xl border border-white/5">
                <TouchableOpacity
                    onPress={() => setActiveTab('ALL')}
                    className={`flex-1 py-2.5 rounded-lg items-center ${activeTab === 'ALL' ? 'bg-[#1e90ff]' : ''}`}
                >
                    <Text className={`text-md font-semibold ${activeTab === 'ALL' ? 'text-white' : 'text-gray-400'}`}>Tất cả</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    onPress={() => setActiveTab('BOOKED')}
                    className={`flex-1 py-2.5 rounded-lg items-center ${activeTab === 'BOOKED' ? 'bg-[#1e90ff]' : ''}`}
                >
                    <Text className={`text-md font-semibold ${activeTab === 'BOOKED' ? 'text-white' : 'text-gray-400'}`}>Đã đặt</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    onPress={() => setActiveTab('CANCELLED')}
                    className={`flex-1 py-2.5 rounded-lg items-center ${activeTab === 'CANCELLED' ? 'bg-[#1e90ff]' : ''}`}
                >
                    <Text className={`text-md font-semibold ${activeTab === 'CANCELLED' ? 'text-white' : 'text-gray-400'}`}>Đã hủy</Text>
                </TouchableOpacity>
            </View>

            {/* --- LIST CONTENT --- */}
            {loading && !refreshing ? (
                <View className="flex-1 justify-center items-center">
                    <ActivityIndicator size="large" color="#1e90ff" />
                    <Text className="text-gray-400 text-xs mt-3">Đang tải lịch sử đặt vé...</Text>
                </View>
            ) : (
                <FlatList
                    data={filteredTickets}
                    keyExtractor={(item) => item.id}
                    renderItem={({ item }) => (
                        <TicketCard
                            item={item}
                            onCancel={() => handleCancelTicket(item.orderCode)}
                        />
                    )}
                    contentContainerStyle={{ paddingBottom: 40 }}
                    showsVerticalScrollIndicator={false}
                    onRefresh={() => loadTickets(true)}
                    refreshing={refreshing}
                    ListEmptyComponent={
                        <View className="mt-10 items-center px-8">
                            <Ticket size={50} color="#8E8E93" />
                            <Text className="text-white text-xl font-bold mt-3">Chưa có vé nào</Text>
                            <Text className="text-gray-400 text-[15px] text-center mt-1 leading-relaxed">
                                Lịch sử mua vé của bạn sẽ xuất hiện tại đây sau khi đặt vé thành công.
                            </Text>
                            <TouchableOpacity
                                onPress={() => router.replace('/(tabs)/schedule')}
                                className="mt-6 bg-[#1e90ff] px-6 py-3 rounded-xl"
                            >
                                <Text className="text-white font-bold text-m">Đặt vé ngay</Text>
                            </TouchableOpacity>
                        </View>
                    }
                />
            )}

            {ConfirmComponent}
            {NotificationComponent}
        </View>
    );
}
