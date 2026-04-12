import axiosClient from './axiosClient';

const authService = {
    signup: (userData) => {
        const url = '/auth/signup';
        // Map data fields to match Spring Boot Entity
        const payload = {
            username: userData.username,
            email: userData.email,
            phoneNumber: userData.phone,
            password: userData.password
        };
        return axiosClient.post(url, payload);
    },

    login: (credentials) => {
        const url = '/auth/login';
        const payload = {
            username: credentials.username,
            password: credentials.password,
            rememberMe: credentials.rememberMe || false
        };
        return axiosClient.post(url, payload);
    },

    refreshToken: (token) => {
        const url = '/auth/refresh';
        return axiosClient.post(url, { refreshToken: token });
    },

    logout: () => {
        const url = '/auth/logout';
        return axiosClient.post(url);
    }

};

export default authService;