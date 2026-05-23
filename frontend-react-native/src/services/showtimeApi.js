import axiosClient from '../api/axiosClient';

const showtimeApi = {
    getById: (id) => {
        const url = `/showtimes/detail/${id}`;
        return axiosClient.get(url, { requireAuth: false });
    },

    getGrouped: (cinemaId, date) => {
        const url = '/showtimes/grouped';
        return axiosClient.get(url, { params: { cinemaId, date }, requireAuth: false });
    },

    getGroupedByMovie: (movieId, params) => {
        const url = `/showtimes/grouped/${movieId}`;
        return axiosClient.get(url, { params, requireAuth: false });
    }
};

export default showtimeApi;