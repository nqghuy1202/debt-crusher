import { apiClient } from "./client";
import type { PayoffCompareRequest, PayoffProjectionResponse } from "./types";

export const payoffApi = {
  compare: (payload: PayoffCompareRequest) =>
    apiClient.post<PayoffProjectionResponse[]>("/payoff/compare", payload).then((res) => res.data),
};
