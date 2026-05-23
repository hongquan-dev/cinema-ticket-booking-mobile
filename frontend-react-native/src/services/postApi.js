import axiosClient from '../api/axiosClient';

const postApi = {
    getAll: (params) => {
        const url = '/posts';
        return axiosClient.get(url, { params, requireAuth: false });
    }
};

export default postApi;