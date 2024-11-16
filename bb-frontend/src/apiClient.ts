import axios from "axios";

const BASE_URL = import.meta.env.VITE_BACKEND_BASE_URL;

export const apiClient = axios.create({
  baseURL: BASE_URL,
});

apiClient.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem("bbToken");
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      localStorage.removeItem("bbToken");
      // window.location.reload();
    }
    return Promise.reject(error);
  }
);
