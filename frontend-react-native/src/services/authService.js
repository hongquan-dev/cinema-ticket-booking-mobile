import axiosClient from '../api/axiosClient';

const authService = {
    signup: (userData) => {
        const url = '/api/auth/signup';
        const payload = {
            username: userData.username,
            email: userData.email,
            phoneNumber: userData.phone,
            password: userData.password
        };
        return axiosClient.post(url, payload);
    },

    login: (credentials) => {
        const url = '/api/auth/login';
        const payload = {
            username: credentials.username,
            password: credentials.password,
            rememberMe: credentials.rememberMe || false
        };
        return axiosClient.post(url, payload);
    },

    refreshToken: (token) => {
        const url = '/api/auth/refresh';
        return axiosClient.post(url, { refreshToken: token });
    },

    logout: () => {
        const url = '/api/auth/logout';
        return axiosClient.post(url);
    }

};

export default authService;