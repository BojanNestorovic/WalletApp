import axios from 'axios';

/**
 * Axios instance configured for session-based authentication.
 * CRITICAL: withCredentials: true is required to send session cookies.
 */
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true, // ESSENTIAL for HttpSession
  headers: {
    'Content-Type': 'application/json'
  }
});

// Response interceptor for error handling
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    // Don't auto-redirect on 401 - let components handle it
    // This prevents issues during login/register flows
    return Promise.reject(error);
  }
);

export default axiosInstance;
