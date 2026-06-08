import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import Constants from 'expo-constants';
import { router } from 'expo-router';
import { Platform } from 'react-native';

const getBaseUrl = () => {
    if (Platform.OS === 'web') return 'http://localhost:8080';
    const hostUri = Constants.expoConfig?.hostUri;
    console.log(hostUri);

    if (hostUri) {
        const ip = hostUri.split(':')[0];
        return `http://${ip}:8080`;
    }
    return 'http://192.168.1.9:8080';
};

const axiosClient = axios.create({
    baseURL: getBaseUrl(),
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
});

const clearAuthAndRedirect = async () => {
    const keys = ['access_token', 'refresh_token', 'user_id', 'user_name', 'user_email', 'user_phone'];
    await AsyncStorage.multiRemove(keys);
    router.replace('/(tabs)/home');
};

axiosClient.interceptors.request.use(
    async (config) => {
        const token = await AsyncStorage.getItem('access_token');
        if (token && config.requireAuth !== false) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

axiosClient.interceptors.response.use(
    (response) => response.data,
    async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {

            if (originalRequest?.requireAuth === false) {
                return Promise.reject(error);
            }

            if (originalRequest.url?.includes('/auth/login')) {
                return Promise.reject(error);
            }

            originalRequest._retry = true;

            try {
                const refreshToken = await AsyncStorage.getItem('refresh_token');

                if (!refreshToken) {
                    await clearAuthAndRedirect();
                    return Promise.reject("No refresh token available");
                }

                const res = await axios.post(`${getBaseUrl()}/auth/refresh`, {
                    refreshToken: refreshToken
                });

                if (res.data && res.data.accessToken) {
                    const newAccessToken = res.data.accessToken;

                    await AsyncStorage.setItem('access_token', newAccessToken);

                    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                    return axiosClient(originalRequest);
                }
            } catch (refreshError) {
                await clearAuthAndRedirect();
                return Promise.reject("Session expired");
            }
        }

        const message = error.response?.data?.message || "Lỗi kết nối Server";
        return Promise.reject(message);
    }
);

export default axiosClient;