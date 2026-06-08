import axiosClient from '../api/axiosClient';

const bookingApi = {
    getSeatLayout: (showtimeId) => {
        const url = `/api/bookings/seat-layout/${showtimeId}`;
        return axiosClient.get(url, { requireAuth: false });
    },

    bookTickets: (bookingData) => {
        const url = '/api/bookings/book';
        return axiosClient.post(url, bookingData);
    },

    getUserTickets: (userId) => {
        const url = `/api/bookings/my-tickets/${userId}`;
        return axiosClient.get(url);
    },

    cancelTicket: (orderCode) => {
        const url = `/api/bookings/cancel/${orderCode}`;
        return axiosClient.put(url);
    }
};

export default bookingApi;