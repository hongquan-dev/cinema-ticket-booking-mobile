import axiosClient from '../api/axiosClient';

const postApi = {
    getAll: (params) => {
        const url = '/posts';
        return axiosClient.get(url, { params, requireAuth: false });
    },

    getById: (id) => {
        const url = `/posts/${id}`;
        return axiosClient.get(url);
    },

    create: (postData, file) => {
        const url = '/posts';
        const formData = new FormData();

        formData.append('post', JSON.stringify(postData));

        if (file) {
            formData.append('file', file);
        }

        return axiosClient.post(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    update: (id, postData, file) => {
        const url = `/posts/${id}`;
        const formData = new FormData();

        formData.append('post', JSON.stringify(postData));

        if (file) {
            formData.append('file', file);
        }

        return axiosClient.put(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    delete: (id) => {
        const url = `/posts/${id}`;
        return axiosClient.delete(url);
    }
};

export default postApi;