import axiosClient from '../api/axiosClient';

const ticketPriceApi = {
    calculatePrice: (params) => {
        const url = '/api/ticket-prices/calculate';
        return axiosClient.get(url, {
            params: {
                movieFormat: params.movieFormat,
                seatType: params.seatType,
                dayType: params.dayType,
                customerType: params.customerType,
                startTime: params.startTime
            },
            requireAuth: false
        });
    }
};

export default ticketPriceApi;