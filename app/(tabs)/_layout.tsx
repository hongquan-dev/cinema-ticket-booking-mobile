import { Tabs } from 'expo-router';
import { CalendarDays, Home, Newspaper, Ticket, UserCircle } from 'lucide-react-native';
import { Platform } from 'react-native';

export default function TabLayout() {
    return (
        <Tabs
            screenOptions={{
                headerShown: false,
                tabBarActiveTintColor: '#FFFFFF',
                tabBarInactiveTintColor: '#8E8E93',
                tabBarStyle: {
                    backgroundColor: '#272b50',
                    borderTopWidth: 1,
                    borderTopColor: '#FFFFFF0D',
                    height: Platform.OS === 'ios' ? 88 : 68,
                    paddingBottom: Platform.OS === 'ios' ? 30 : 12,
                    paddingTop: 10,
                },
                tabBarLabelStyle: {
                    fontSize: 13,
                    fontWeight: '400',
                },
            }}
        >
            <Tabs.Screen name="home" options={{ title: 'Trang chủ', tabBarIcon: ({ color }) => <Home size={24} color={color} /> }} />
            <Tabs.Screen name="schedule" options={{ title: 'Lịch phim', tabBarIcon: ({ color }) => <CalendarDays size={24} color={color} /> }} />
            <Tabs.Screen name="news" options={{ title: 'Tin tức', tabBarIcon: ({ color }) => <Newspaper size={24} color={color} /> }} />
            <Tabs.Screen name="tickets" options={{ title: 'Vé đã mua', tabBarIcon: ({ color }) => <Ticket size={24} color={color} /> }} />
            <Tabs.Screen name="account" options={{ title: 'Tài khoản', tabBarIcon: ({ color }) => <UserCircle size={24} color={color} /> }} />
        </Tabs>
    );
}