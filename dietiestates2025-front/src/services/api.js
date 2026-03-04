import axios from 'axios';

const api = axios.create({
    //baseURL: 'http://localhost:8080/api'
    baseURL: 'https://dietiestates2025backendv2-hfehdnhuabfefmg7.italynorth-01.azurewebsites.net/api'
    //baseURL: 'https://dietiestates2025backend-d6fwbjhfhgebe2c7.canadacentral-01.azurewebsites.net/api'
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); 
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;