import axiosClient from '../api/axiosClient';

const movieApi = {
    getComingSoon: (date) => {
        const url = '/api/movies/coming-soon';
        return axiosClient.get(url, { params: { date }, requireAuth: false });
    }
};

export default movieApi;