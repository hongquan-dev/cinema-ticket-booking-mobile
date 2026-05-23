import axiosClient from '../api/axiosClient';

const movieApi = {
    getComingSoon: (date) => {
        const url = '/movies/coming-soon';
        return axiosClient.get(url, { params: { date }, requireAuth: false });
    }
};

export default movieApi;