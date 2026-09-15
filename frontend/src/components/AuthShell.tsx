import { Flame } from "lucide-react";
import type { ReactNode } from "react";
import { LanguageToggle } from "./LanguageToggle";

interface AuthShellProps {
  title: string;
  subtitle: string;
  children: ReactNode;
}

export function AuthShell({ title, subtitle, children }: AuthShellProps) {
  return (
    <div className="relative flex min-h-screen items-center justify-center bg-slate-50 px-4 py-12 dark:bg-slate-950">
      <div className="absolute top-4 right-4">
        <LanguageToggle />
      </div>

      <div className="w-full max-w-sm">
        <div className="mb-6 flex items-center gap-2.5">
          <span className="flex h-8 w-8 items-center justify-center rounded-md bg-brand-500 text-white">
            <Flame size={16} />
          </span>
          <span className="text-base font-bold tracking-tight text-slate-900 dark:text-slate-100">HL Balance</span>
        </div>

        <div className="rounded-lg border border-slate-200 bg-white p-7 dark:border-slate-800 dark:bg-slate-900">
          <h1 className="text-lg font-semibold text-slate-900 dark:text-slate-100">{title}</h1>
          <p className="mt-1 mb-6 text-sm text-slate-500 dark:text-slate-400">{subtitle}</p>
          {children}
        </div>
      </div>
    </div>
  );
}
