import axiosClient from "../api/axiosClient";

const movieShowDateApi = {
    // Get all show dates
    getAll: () => {
        const url = "/movie-show-dates";
        return axiosClient.get(url, { requireAuth: false });
    },
};

export default movieShowDateApi;