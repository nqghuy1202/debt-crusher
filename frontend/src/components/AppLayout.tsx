import { Flame, LogOut, TrendingDown, Wallet } from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useLanguage } from "../context/LanguageContext";
import { ThemeToggle } from "./ThemeToggle";
import { LanguageToggle } from "./LanguageToggle";

const navLinkBase =
  "flex items-center gap-1.5 border-b-2 px-1 py-4 text-sm font-medium transition";
const navLinkActive = "border-brand-500 text-slate-900 dark:text-slate-100";
const navLinkInactive =
  "border-transparent text-slate-500 hover:text-slate-800 dark:text-slate-400 dark:hover:text-slate-200";

export function AppLayout() {
  const { email, logout } = useAuth();
  const { t } = useLanguage();

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950">
      <header className="sticky top-0 z-40 border-b border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-950">
        <div className="mx-auto flex max-w-5xl flex-wrap items-center justify-between gap-x-8 px-4 sm:px-6">
          <div className="flex items-center gap-8">
            <span className="flex items-center gap-2 py-4 text-base font-bold tracking-tight text-slate-900 dark:text-slate-100">
              <span className="flex h-7 w-7 items-center justify-center rounded-md bg-brand-500 text-white">
                <Flame size={15} />
              </span>
              HL Balance
            </span>
            <nav className="flex items-center gap-6">
              <NavLink
                to="/debts"
                className={({ isActive }) => `${navLinkBase} ${isActive ? navLinkActive : navLinkInactive}`}
              >
                <Wallet size={15} />
                {t("nav.debts")}
              </NavLink>
              <NavLink
                to="/payoff/compare"
                className={({ isActive }) => `${navLinkBase} ${isActive ? navLinkActive : navLinkInactive}`}
              >
                <TrendingDown size={15} />
                {t("nav.payoffCompare")}
              </NavLink>
            </nav>
          </div>
          <div className="flex items-center gap-2 py-2">
            <LanguageToggle />
            <ThemeToggle />
            <span className="hidden px-1 text-sm text-slate-500 sm:inline dark:text-slate-400">{email}</span>
            <button
              type="button"
              onClick={logout}
              title={t("nav.logout")}
              className="inline-flex items-center gap-1.5 rounded-md border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
            >
              <LogOut size={14} />
              <span className="hidden sm:inline">{t("nav.logout")}</span>
            </button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-5xl px-4 py-6 sm:px-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  );
}
