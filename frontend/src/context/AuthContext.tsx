import { createContext, useContext, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { AuthResponse } from "../api/types";

interface AuthState {
  email: string | null;
  isAuthenticated: boolean;
  login: (auth: AuthResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthState | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [email, setEmail] = useState<string | null>(() => localStorage.getItem("email"));

  const login = (auth: AuthResponse) => {
    localStorage.setItem("accessToken", auth.accessToken);
    localStorage.setItem("email", auth.email);
    setEmail(auth.email);
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("email");
    setEmail(null);
  };

  const value = useMemo(
    () => ({ email, isAuthenticated: email !== null, login, logout }),
    [email],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth phải được dùng bên trong AuthProvider");
  }
  return ctx;
}
