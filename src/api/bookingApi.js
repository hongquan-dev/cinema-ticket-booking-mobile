import axiosClient from './axiosClient';

const bookingApi = {
    getSeatLayout: (showtimeId) => {
        const url = `/bookings/seat-layout/${showtimeId}`;
        return axiosClient.get(url);
    },

    bookTickets: (bookingData) => {
        const url = '/bookings/book';
        return axiosClient.post(url, bookingData);
    },

    getUserTickets: (userId, page = 1, size = 10) => {
        const url = `/bookings/user/${userId}`;
        return axiosClient.get(url, {
            params: { page, size }
        });
    },

    cancelTicket: (ticketId, userId) => {
        const url = `/bookings/${ticketId}`;
        return axiosClient.delete(url, {
            params: { userId }
        });
    }
};

export default bookingApi;