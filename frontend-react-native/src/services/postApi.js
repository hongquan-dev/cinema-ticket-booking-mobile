import axiosClient from '../api/axiosClient';

const postApi = {
    getAll: (params) => {
        const url = '/api/posts';
        return axiosClient.get(url, { params, requireAuth: false });
    }
};

export default postApi;