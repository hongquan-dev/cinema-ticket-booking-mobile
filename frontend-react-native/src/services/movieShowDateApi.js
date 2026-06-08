import axiosClient from "../api/axiosClient";

const movieShowDateApi = {
    getAll: () => {
        const url = "/api/movie-show-dates";
        return axiosClient.get(url, { requireAuth: false });
    },
};

export default movieShowDateApi;