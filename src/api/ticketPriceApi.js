import axiosClient from './axiosClient';

const ticketPriceApi = {

    getTicketPrices: (page = 1, size = 10) => {
        const url = '/ticket-prices';
        return axiosClient.get(url, {
            params: { page, size }
        });
    },

    create: (data) => {
        const url = '/ticket-prices';
        return axiosClient.post(url, data);
    },

    update: (id, data) => {
        const url = `/ticket-prices/${id}`;
        return axiosClient.put(url, data);
    },

    delete: (id) => {
        const url = `/ticket-prices/${id}`;
        return axiosClient.delete(url);
    },

    calculatePrice: (params) => {
        const url = '/ticket-prices/calculate';
        return axiosClient.get(url, {
            params: {
                movieFormat: params.movieFormat,
                seatType: params.seatType,
                dayType: params.dayType,
                customerType: params.customerType,
                startTime: params.startTime
            }
        });
    }
};

export default ticketPriceApi;