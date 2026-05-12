import axiosClient from "../api/axiosClient";

const movieShowDateApi = {
    create: (data) => {
        const url = "/movie-show-dates";
        return axiosClient.post(url, data);
    },

    // Get all show dates
    getAll: () => {
        const url = "/movie-show-dates";
        return axiosClient.get(url);
    },

    update: (id, data) => {
        const url = `/movie-show-dates/${id}`;
        return axiosClient.put(url, data);
    },

    delete: (id) => {
        const url = `/movie-show-dates/${id}`;
        return axiosClient.delete(url);
    },

    deleteAll: () => {
        const url = "/movie-show-dates/all";
        return axiosClient.delete(url);
    },
};

export default movieShowDateApi;