import { apiClient } from "./client";
import type { AuthResponse, LoginRequest, RegisterRequest } from "./types";

export const authApi = {
  register: (payload: RegisterRequest) =>
    apiClient.post<AuthResponse>("/auth/register", payload).then((res) => res.data),

  login: (payload: LoginRequest) =>
    apiClient.post<AuthResponse>("/auth/login", payload).then((res) => res.data),
};
