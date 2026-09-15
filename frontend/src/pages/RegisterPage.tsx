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

export function RegisterPage() {
  const { t } = useLanguage();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    if (password !== confirmPassword) {
      setError(t("auth.passwordMismatch"));
      return;
    }
    setIsSubmitting(true);
    try {
      const auth = await authApi.register({ email, password });
      login(auth);
      navigate("/debts");
    } catch (err) {
      setError(err instanceof Error ? err.message : t("auth.registerFailed"));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AuthShell title={t("auth.register.title")} subtitle={t("auth.register.subtitle")}>
      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
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
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            placeholder={t("auth.emailPlaceholder")}
            className={inputClass}
          />
        </label>
        <label className="flex flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300">
          {t("auth.password")}
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
            autoComplete="new-password"
            placeholder="••••••••"
            className={inputClass}
          />
          <span className="text-xs font-normal text-slate-400">{t("auth.passwordMinHint")}</span>
        </label>
        <label className="flex flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300">
          {t("auth.confirmPassword")}
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
            minLength={8}
            autoComplete="new-password"
            placeholder="••••••••"
            className={inputClass}
          />
        </label>
        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-1 rounded-md bg-brand-500 py-2.5 text-sm font-semibold text-white transition hover:bg-brand-600 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting ? t("auth.registerButtonLoading") : t("auth.registerButton")}
        </button>
        <p className="text-center text-sm text-slate-500 dark:text-slate-400">
          {t("auth.hasAccount")}{" "}
          <Link to="/login" className="font-medium text-brand-600 hover:underline dark:text-brand-400">
            {t("auth.loginLink")}
          </Link>
        </p>
      </form>
    </AuthShell>
  );
}
