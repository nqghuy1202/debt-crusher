import type { DebtCategory } from "../api/types";

export const DEBT_CATEGORIES: DebtCategory[] = [
  "CREDIT_CARD",
  "CONSUMER_LOAN",
  "INSTALLMENT",
  "STUDENT_LOAN",
  "MORTGAGE",
  "OTHER",
];

/** Class Tailwind cho badge màu theo phân loại — nhãn hiển thị lấy qua t(`category.${category}`). */
export const CATEGORY_BADGE_CLASS: Record<DebtCategory, string> = {
  CREDIT_CARD: "border-violet-200 bg-violet-50 text-violet-700 dark:border-violet-500/30 dark:bg-violet-500/10 dark:text-violet-400",
  CONSUMER_LOAN: "border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-400",
  INSTALLMENT: "border-cyan-200 bg-cyan-50 text-cyan-700 dark:border-cyan-500/30 dark:bg-cyan-500/10 dark:text-cyan-400",
  STUDENT_LOAN: "border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-500/30 dark:bg-emerald-500/10 dark:text-emerald-400",
  MORTGAGE: "border-indigo-200 bg-indigo-50 text-indigo-700 dark:border-indigo-500/30 dark:bg-indigo-500/10 dark:text-indigo-400",
  OTHER: "border-slate-200 bg-slate-50 text-slate-600 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-400",
};
