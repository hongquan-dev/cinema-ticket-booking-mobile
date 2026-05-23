import axiosClient from '../api/axiosClient';

const bookingApi = {
    getSeatLayout: (showtimeId) => {
        const url = `/bookings/seat-layout/${showtimeId}`;
        return axiosClient.get(url, { requireAuth: false });
    },

    bookTickets: (bookingData) => {
        const url = '/bookings/book';
        return axiosClient.post(url, bookingData);
    },

    getUserTickets: (userId) => {
        const url = `/bookings/my-tickets/${userId}`;
        return axiosClient.get(url);
    },

    cancelTicket: (orderCode) => {
        const url = `/bookings/cancel/${orderCode}`;
        return axiosClient.put(url);
    }
};

export default bookingApi;