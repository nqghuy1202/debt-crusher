import { useState } from "react";
import type { FormEvent } from "react";
import { TriangleAlert } from "lucide-react";
import type { DebtCategory, DebtRequest } from "../api/types";
import { DEBT_CATEGORIES } from "../utils/category";
import { useLanguage } from "../context/LanguageContext";
import { FormattedNumberInput } from "./FormattedNumberInput";

interface DebtFormProps {
  initialValue?: DebtRequest;
  submitLabel: string;
  onSubmit: (payload: DebtRequest) => Promise<void>;
  onCancel: () => void;
}

const emptyForm = {
  name: "",
  balance: "",
  annualInterestRatePercent: "",
  minimumMonthlyPayment: "",
  category: "OTHER" as DebtCategory,
};

const inputClass =
  "w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 placeholder:text-slate-400 focus:border-brand-500 focus:ring-1 focus:ring-brand-500/30 focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100";
const labelClass = "flex flex-1 flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300";

export function DebtForm({ initialValue, submitLabel, onSubmit, onCancel }: DebtFormProps) {
  const { t } = useLanguage();
  const [form, setForm] = useState(() =>
    initialValue
      ? {
          name: initialValue.name,
          balance: String(initialValue.balance),
          annualInterestRatePercent: String(initialValue.annualInterestRatePercent),
          minimumMonthlyPayment: String(initialValue.minimumMonthlyPayment),
          category: initialValue.category,
        }
      : emptyForm,
  );
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    const balance = Number(form.balance);
    const annualInterestRatePercent = Number(form.annualInterestRatePercent);
    const minimumMonthlyPayment = Number(form.minimumMonthlyPayment);
    if (!form.name.trim() || Number.isNaN(balance) || Number.isNaN(annualInterestRatePercent) || Number.isNaN(minimumMonthlyPayment)) {
      setError(t("debtForm.validationError"));
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit({
        name: form.name.trim(),
        balance,
        annualInterestRatePercent,
        minimumMonthlyPayment,
        category: form.category,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : t("debtForm.genericError"));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
      {error && (
        <p className="flex items-start gap-2 rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:border-rose-500/30 dark:bg-rose-500/10 dark:text-rose-400">
          <TriangleAlert size={16} className="mt-0.5 shrink-0" />
          {error}
        </p>
      )}
      <label className={labelClass}>
        {t("debtForm.name")}
        <input
          className={inputClass}
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          placeholder={t("debtForm.namePlaceholder")}
          required
        />
      </label>
      <div className="flex flex-col gap-4 sm:flex-row">
        <label className={labelClass}>
          {t("debtForm.balance")}
          <FormattedNumberInput
            className={inputClass}
            value={form.balance}
            onChange={(raw) => setForm({ ...form, balance: raw })}
            required
          />
        </label>
        <label className={labelClass}>
          {t("debtForm.rate")}
          <input
            type="number"
            min="0"
            step="0.01"
            className={inputClass}
            value={form.annualInterestRatePercent}
            onChange={(e) => setForm({ ...form, annualInterestRatePercent: e.target.value })}
            required
          />
        </label>
      </div>
      <div className="flex flex-col gap-4 sm:flex-row">
        <label className={labelClass}>
          {t("debtForm.minPayment")}
          <FormattedNumberInput
            className={inputClass}
            value={form.minimumMonthlyPayment}
            onChange={(raw) => setForm({ ...form, minimumMonthlyPayment: raw })}
            required
          />
        </label>
        <label className={labelClass}>
          {t("debtForm.category")}
          <select
            className={`${inputClass} select-arrow pr-8`}
            value={form.category}
            onChange={(e) => setForm({ ...form, category: e.target.value as DebtCategory })}
          >
            {DEBT_CATEGORIES.map((category) => (
              <option key={category} value={category}>
                {t(`category.${category}`)}
              </option>
            ))}
          </select>
        </label>
      </div>
      <div className="mt-1 flex justify-end gap-2">
        <button
          type="button"
          onClick={onCancel}
          disabled={isSubmitting}
          className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
        >
          {t("debtForm.cancel")}
        </button>
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-md bg-brand-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-600 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting ? t("debtForm.saving") : submitLabel}
        </button>
      </div>
    </form>
  );
}
