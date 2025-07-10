import axios, { AxiosRequestConfig, AxiosInstance } from "axios";

/**
 * Create an Axios instance with dynamic baseURL and interceptors
 */
function createAxiosInstance(): AxiosInstance {
    const isServer = typeof window === "undefined";
    const baseURL = isServer
        ? "http://techinterview-backend:8101"
        : process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8101";
    console.log(baseURL)
    const instance = axios.create({
        baseURL,
        timeout: 600000,
        withCredentials: true,
    });

    // Request interceptor
    instance.interceptors.request.use(
        function (config) {
            return config;
        },
        function (error) {
            return Promise.reject(error);
        }
    );

    // Response interceptor
    instance.interceptors.response.use(
        function (response) {
            const { data } = response;

            // Not logged in
            if (data.code === 40100 && typeof window !== "undefined") {
                if (
                    !response.request.responseURL.includes("user/get/login") &&
                    !window.location.pathname.includes("/user/login")
                ) {
                    window.location.href = `/user/login?redirect=${window.location.href}`;
                }
            } else if (data.code !== 0) {
                throw new Error(data.message ?? "Server error");
            }

            return data;
        },
        function (error) {
            return Promise.reject(error);
        }
    );

    return instance;
}

/**
 * Public request wrapper
 */
export async function request<T = any>(
    url: string,
    config: AxiosRequestConfig = {}
): Promise<{ data: T }> {
    const axiosInstance = createAxiosInstance();
    return axiosInstance.request<T>({
        url,
        ...config,
    });
}
