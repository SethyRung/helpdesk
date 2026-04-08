import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type InternalAxiosRequestConfig,
} from "axios";

import { ApiResponseCode, type ApiResponse } from "@/types";

const client: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
});

client.interceptors.response.use(
  async (response) => {
    const requestConfig = response.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
      skipAuthRefresh?: boolean;
    };
    const payload = response.data as ApiResponse<unknown> | undefined;

    if (
      !requestConfig.skipAuthRefresh &&
      !requestConfig._retry &&
      payload?.status.code === ApiResponseCode.Unauthorized
    ) {
      requestConfig._retry = true;
      try {
        const refreshResponse = await client.post<ApiResponse<string>>("/auth/refresh", undefined, {
          skipAuthRefresh: true,
        } as AxiosRequestConfig);

        if (refreshResponse.data.status.code !== ApiResponseCode.Success) {
          throw new Error(response.data.status.message);
        }
        return await client.request(requestConfig);
      } catch (error) {
        await client.post("/auth/logout", undefined, {
          skipAuthRefresh: true,
        } as AxiosRequestConfig);
        throw error;
      }
    }

    return response;
  },
  async (error) => Promise.reject(error),
);

export const api = {
  get: async <T>(path: string, config?: AxiosRequestConfig) => {
    const res = await client.get<ApiResponse<T>>(path, config);
    return res.data;
  },

  post: async <T>(path: string, body?: unknown, config?: AxiosRequestConfig) => {
    const res = await client.post<ApiResponse<T>>(path, body, config);
    return res.data;
  },

  put: async <T>(path: string, body?: unknown, config?: AxiosRequestConfig) => {
    const res = await client.put<ApiResponse<T>>(path, body, config);
    return res.data;
  },

  patch: async <T>(path: string, body?: unknown, config?: AxiosRequestConfig) => {
    const res = await client.patch<ApiResponse<T>>(path, body, config);
    return res.data;
  },

  delete: async <T>(path: string, config?: AxiosRequestConfig) => {
    const res = await client.delete<ApiResponse<T>>(path, config);
    return res.data;
  },
};
