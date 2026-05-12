import axiosClient from '../api/axiosClient';

const showtimeApi = {
    getAll: (params) => {
        const url = '/showtimes';
        return axiosClient.get(url, { params });
    },

    create: (data) => {
        const url = '/showtimes';
        return axiosClient.post(url, data);
    },

    getByRoom: (roomId, page = 1, size = 5) => {
        const url = `/showtimes/room/${roomId}`;
        return axiosClient.get(url, { params: { page, size } });
    },

    getByCinema: (cinemaId, page = 1, size = 10) => {
        const url = `/showtimes/cinema/${cinemaId}`;
        return axiosClient.get(url, { params: { page, size } });
    },

    getById: (id) => {
        const url = `/showtimes/detail/${id}`;
        return axiosClient.get(url);
    },

    update: (id, data) => {
        const url = `/showtimes/${id}`;
        return axiosClient.put(url, data);
    },


    delete: (id) => {
        const url = `/showtimes/${id}`;
        return axiosClient.delete(url);
    },

    getGrouped: (cinemaId, date) => {
        const url = '/showtimes/grouped';
        return axiosClient.get(url, { params: { cinemaId, date } });
    },

    getGroupedByMovie: (movieId, params) => {
        const url = `/showtimes/grouped/${movieId}`;
        return axiosClient.get(url, { params });
    }
};

export default showtimeApi;