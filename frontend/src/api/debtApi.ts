import { apiClient } from "./client";
import type { DebtRequest, DebtResponse } from "./types";

export const debtApi = {
  list: () => apiClient.get<DebtResponse[]>("/debts").then((res) => res.data),

  create: (payload: DebtRequest) =>
    apiClient.post<DebtResponse>("/debts", payload).then((res) => res.data),

  update: (debtId: number, payload: DebtRequest) =>
    apiClient.put<DebtResponse>(`/debts/${debtId}`, payload).then((res) => res.data),

  remove: (debtId: number) => apiClient.delete<void>(`/debts/${debtId}`).then(() => undefined),
};
