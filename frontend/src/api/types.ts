// Khớp 1:1 với các DTO record ở debtcrusher-controller/.../model/dto

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
  email: string;
}

export type DebtCategory = "CREDIT_CARD" | "CONSUMER_LOAN" | "INSTALLMENT" | "STUDENT_LOAN" | "MORTGAGE" | "OTHER";

/** Dùng chung cho tạo (POST /debts) và sửa (PUT /debts/{id}) — số nhập từ form ở dạng string. */
export interface DebtRequest {
  name: string;
  balance: number;
  annualInterestRatePercent: number;
  minimumMonthlyPayment: number;
  category: DebtCategory;
}

export interface DebtResponse {
  id: number;
  userId: number;
  name: string;
  balance: number;
  annualInterestRatePercent: number;
  minimumMonthlyPayment: number;
  category: DebtCategory;
  createdAt: string;
}

export type PayoffStrategyType = "AVALANCHE" | "SNOWBALL" | "MINIMUM_ONLY";

export interface PayoffCompareRequest {
  extraMonthlyPayment: number;
}

export interface PayoffProjectionResponse {
  strategy: PayoffStrategyType;
  totalMonths: number;
  totalInterestPaid: number;
  payoffOrder: string[];
}
