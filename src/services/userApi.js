import axiosClient from '../api/axiosClient';

const userApi = {

    getById: (id) => {
        const url = `/users/${id}`;
        return axiosClient.get(url);
    },

    update: (id, updateData) => {
        const url = `/users/${id}`;
        return axiosClient.put(url, updateData);
    },

    changePassword: (userId, passwordData) => {
        const url = `/users/change-password/${userId}`;
        return axiosClient.post(url, passwordData);
    }
};

export default userApi;