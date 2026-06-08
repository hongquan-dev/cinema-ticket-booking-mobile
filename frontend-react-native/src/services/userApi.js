import axiosClient from '../api/axiosClient';

const userApi = {
    getById: (id) => {
        const url = `/api/users/${id}`;
        return axiosClient.get(url);
    },

    update: (id, updateData) => {
        const url = `/api/users/${id}`;
        return axiosClient.put(url, updateData);
    },

    changePassword: (userId, passwordData) => {
        const url = `/api/users/change-password/${userId}`;
        return axiosClient.post(url, passwordData);
    },

    updateAvatar: (id, formData) => {
        const url = `/api/users/${id}/avatar`;
        return axiosClient.put(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    sendVerificationEmail: (id) => {
        const url = `/api/users/${id}/send-verification`;
        return axiosClient.post(url);
    }
};

export default userApi;