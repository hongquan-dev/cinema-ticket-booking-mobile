import axiosClient from '../api/axiosClient';

const movieApi = {
    getAll: (params) => {
        const url = '/movies';
        return axiosClient.get(url, { params });
    },

    getById: (id) => {
        const url = `/movies/${id}`;
        return axiosClient.get(url);
    },

    create: (movieData, file) => {
        const url = '/movies';
        const formData = new FormData();

        formData.append('movie', JSON.stringify(movieData));

        if (file) {
            formData.append('file', file);
        }

        return axiosClient.post(url, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    },

    update: (id, movieData, file) => {
        const url = `/movies/${id}`;
        const formData = new FormData();

        formData.append('movie', JSON.stringify(movieData));

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
        const url = `/movies/${id}`;
        return axiosClient.delete(url);
    },

    getComingSoon: (date) => {
        const url = '/movies/coming-soon';
        return axiosClient.get(url, { params: { date } });
    }
};

export default movieApi;