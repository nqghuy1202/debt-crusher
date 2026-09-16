import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { TriangleAlert } from "lucide-react";
import { authApi } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import { useLanguage } from "../context/LanguageContext";
import { AuthShell } from "../components/AuthShell";

const inputClass =
  "w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder:text-slate-400 focus:border-brand-500 focus:ring-1 focus:ring-brand-500/30 focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100";
const inputErrorClass =
  "border-rose-400 focus:border-rose-500 focus:ring-rose-500/30 dark:border-rose-500/60";

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

type FieldErrors = { email?: string; password?: string };

// Tài khoản demo có sẵn 20 khoản nợ mẫu, tự seed lúc backend khởi động nếu chưa có (xem
// DemoDataSeeder ở debtcrusher-application) — cho phép người xem thử app không cần tự đăng ký/nhập liệu.
const DEMO_CREDENTIALS = { email: "demo@debtcrusher.vn", password: "Demo@2026" };

export function LoginPage() {
  const { t } = useLanguage();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const validate = (credentials: { email: string; password: string }): FieldErrors => {
    const errors: FieldErrors = {};
    if (!credentials.email.trim()) {
      errors.email = t("auth.errors.emailRequired");
    } else if (!EMAIL_PATTERN.test(credentials.email.trim())) {
      errors.email = t("auth.errors.emailInvalid");
    }
    if (!credentials.password) {
      errors.password = t("auth.errors.passwordRequired");
    }
    return errors;
  };

  const submitLogin = async (credentials: { email: string; password: string }) => {
    setError(null);
    setIsSubmitting(true);
    try {
      const auth = await authApi.login(credentials);
      login(auth);
      navigate("/debts");
    } catch (err) {
      setError(err instanceof Error ? err.message : t("auth.loginFailed"));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const credentials = { email, password };
    const errors = validate(credentials);
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) return;
    submitLogin(credentials);
  };

  const handleDemoLogin = () => {
    setFieldErrors({});
    submitLogin(DEMO_CREDENTIALS);
  };

  return (
    <AuthShell title={t("auth.login.title")} subtitle={t("auth.login.subtitle")}>
      <form className="flex flex-col gap-4" onSubmit={handleSubmit} autoComplete="off" noValidate>
        {error && (
          <p className="flex items-start gap-2 rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:border-rose-500/30 dark:bg-rose-500/10 dark:text-rose-400">
            <TriangleAlert size={16} className="mt-0.5 shrink-0" />
            {error}
          </p>
        )}
        <label className="flex flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300">
          {t("auth.email")}
          <input
            type="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (fieldErrors.email) setFieldErrors((prev) => ({ ...prev, email: undefined }));
            }}
            autoComplete="off"
            placeholder={t("auth.emailPlaceholder")}
            aria-invalid={!!fieldErrors.email}
            className={`${inputClass} ${fieldErrors.email ? inputErrorClass : ""}`}
          />
          {fieldErrors.email && <span className="text-xs font-normal text-rose-600 dark:text-rose-400">{fieldErrors.email}</span>}
        </label>
        <label className="flex flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300">
          {t("auth.password")}
          <input
            type="password"
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
              if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: undefined }));
            }}
            autoComplete="new-password"
            placeholder="••••••••"
            aria-invalid={!!fieldErrors.password}
            className={`${inputClass} ${fieldErrors.password ? inputErrorClass : ""}`}
          />
          {fieldErrors.password && <span className="text-xs font-normal text-rose-600 dark:text-rose-400">{fieldErrors.password}</span>}
        </label>
        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-1 rounded-md bg-brand-500 py-2.5 text-sm font-semibold text-white transition hover:bg-brand-600 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting ? t("auth.loginButtonLoading") : t("auth.loginButton")}
        </button>
        <div className="flex items-center gap-3 text-xs text-slate-400">
          <span className="h-px flex-1 bg-slate-200 dark:bg-slate-800" />
          {t("auth.or")}
          <span className="h-px flex-1 bg-slate-200 dark:bg-slate-800" />
        </div>
        <button
          type="button"
          onClick={handleDemoLogin}
          disabled={isSubmitting}
          className="rounded-md border border-slate-300 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-900"
        >
          {isSubmitting ? t("auth.demoLoginButtonLoading") : t("auth.demoLoginButton")}
        </button>
        <p className="text-center text-sm text-slate-500 dark:text-slate-400">
          {t("auth.noAccount")}{" "}
          <Link to="/register" className="font-medium text-brand-600 hover:underline dark:text-brand-400">
            {t("auth.registerLink")}
          </Link>
        </p>
      </form>
    </AuthShell>
  );
}
