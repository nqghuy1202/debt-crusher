import { useState } from "react";
import type { FormEvent } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Award, TriangleAlert } from "lucide-react";
import { payoffApi } from "../api/payoffApi";
import type { PayoffProjectionResponse } from "../api/types";
import { PayoffBarChart } from "../components/PayoffBarChart";
import { FormattedNumberInput } from "../components/FormattedNumberInput";
import { getStrategyMeta } from "../utils/strategy";
import { useLanguage } from "../context/LanguageContext";

const currencyFormatter = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

export function PayoffComparePage() {
  const { t } = useLanguage();
  const strategyMeta = getStrategyMeta(t);
  const [extraMonthlyPayment, setExtraMonthlyPayment] = useState("0");
  const [projections, setProjections] = useState<PayoffProjectionResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    const extra = Number(extraMonthlyPayment);
    if (Number.isNaN(extra) || extra < 0) {
      setError(t("payoff.negativeError"));
      return;
    }

    setIsSubmitting(true);
    try {
      setProjections(await payoffApi.compare({ extraMonthlyPayment: extra }));
    } catch (err) {
      setProjections(null);
      setError(err instanceof Error ? err.message : t("payoff.compareFailed"));
    } finally {
      setIsSubmitting(false);
    }
  };

  const bestStrategy = projections?.reduce((best, p) =>
    p.totalInterestPaid < best.totalInterestPaid ? p : best,
  );

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100">{t("payoff.title")}</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">{t("payoff.subtitle")}</p>
        </div>
        <Link
          to="/debts"
          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-300 px-3.5 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
        >
          <ArrowLeft size={16} />
          {t("payoff.backLink")}
        </Link>
      </div>

      <form
        className="flex flex-wrap items-end gap-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-800 dark:bg-slate-900"
        onSubmit={handleSubmit}
      >
        <label className="flex min-w-56 flex-1 flex-col gap-1.5 text-sm font-medium text-slate-700 dark:text-slate-300">
          {t("payoff.extraLabel")}
          <FormattedNumberInput
            value={extraMonthlyPayment}
            onChange={setExtraMonthlyPayment}
            required
            className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/30 focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          />
        </label>
        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-lg bg-gradient-to-r from-blue-600 to-blue-500 px-4 py-2 text-sm font-semibold text-white shadow-sm shadow-blue-600/30 transition hover:from-blue-500 hover:to-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting ? t("payoff.comparing") : t("payoff.compareButton")}
        </button>
      </form>

      {error && (
        <p className="flex items-start gap-2 rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:bg-rose-500/10 dark:text-rose-400">
          <TriangleAlert size={16} className="mt-0.5 shrink-0" />
          {error}
        </p>
      )}

      {projections && (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
            {projections.map((p) => {
              const meta = strategyMeta[p.strategy];
              const Icon = meta.icon;
              const isBest = bestStrategy?.strategy === p.strategy;
              return (
                <div
                  key={p.strategy}
                  className={`relative flex flex-col gap-3 rounded-2xl border bg-white p-5 shadow-sm dark:bg-slate-900 ${
                    isBest
                      ? "border-emerald-300 ring-1 ring-emerald-300 dark:border-emerald-500/40 dark:ring-emerald-500/30"
                      : "border-slate-200 dark:border-slate-800"
                  }`}
                >
                  {isBest && (
                    <span className="absolute -top-3 right-4 inline-flex items-center gap-1 rounded-full bg-emerald-500 px-2.5 py-1 text-xs font-semibold text-white shadow-sm">
                      <Award size={12} />
                      {t("payoff.best")}
                    </span>
                  )}
                  <div className="flex items-center gap-2 font-semibold text-slate-900 dark:text-slate-100">
                    <span
                      className="flex h-8 w-8 items-center justify-center rounded-lg"
                      style={{ background: `${meta.color}1a`, color: meta.color }}
                    >
                      <Icon size={16} />
                    </span>
                    {meta.label}
                  </div>
                  <p className="text-sm text-slate-500 dark:text-slate-400">{meta.description}</p>
                  <dl className="my-1 flex flex-col gap-2 border-t border-slate-100 pt-3 dark:border-slate-800">
                    <div className="flex justify-between text-sm">
                      <dt className="text-slate-500 dark:text-slate-400">{t("payoff.months")}</dt>
                      <dd className="font-semibold tabular-nums text-slate-900 dark:text-slate-100">
                        {p.totalMonths} {t("payoff.monthsSuffix")}
                      </dd>
                    </div>
                    <div className="flex justify-between text-sm">
                      <dt className="text-slate-500 dark:text-slate-400">{t("payoff.totalInterest")}</dt>
                      <dd className="font-semibold tabular-nums text-slate-900 dark:text-slate-100">
                        {currencyFormatter.format(p.totalInterestPaid)}
                      </dd>
                    </div>
                  </dl>
                  {p.payoffOrder.length > 0 && (
                    <div>
                      <p className="mb-1 text-xs text-slate-400">{t("payoff.order")}</p>
                      <ol className="flex flex-col gap-0.5 pl-4 text-sm text-slate-600 dark:text-slate-300">
                        {p.payoffOrder.map((name) => (
                          <li key={name} className="list-decimal">
                            {name}
                          </li>
                        ))}
                      </ol>
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          <div>
            <h2 className="mb-3 text-base font-semibold text-slate-900 dark:text-slate-100">{t("payoff.chartTitle")}</h2>
            <PayoffBarChart projections={projections} />
          </div>

          <details className="group rounded-2xl border border-slate-200 bg-white p-5 text-sm shadow-sm dark:border-slate-800 dark:bg-slate-900">
            <summary className="cursor-pointer font-medium text-slate-700 select-none dark:text-slate-300">
              {t("payoff.viewTable")}
            </summary>
            <div className="mt-4 overflow-x-auto">
              <table className="w-full min-w-[420px] text-sm">
                <thead>
                  <tr className="border-b border-slate-200 text-left text-xs font-medium tracking-wide text-slate-500 uppercase dark:border-slate-800 dark:text-slate-400">
                    <th className="py-2 pr-4">{t("payoff.col.strategy")}</th>
                    <th className="py-2 pr-4">{t("payoff.col.months")}</th>
                    <th className="py-2">{t("payoff.col.interest")}</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                  {projections.map((p) => (
                    <tr key={p.strategy}>
                      <td className="py-2 pr-4 font-medium text-slate-900 dark:text-slate-100">{strategyMeta[p.strategy].label}</td>
                      <td className="py-2 pr-4 tabular-nums text-slate-700 dark:text-slate-300">
                        {p.totalMonths} {t("payoff.monthsSuffix")}
                      </td>
                      <td className="py-2 tabular-nums text-slate-700 dark:text-slate-300">
                        {currencyFormatter.format(p.totalInterestPaid)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </details>
        </>
      )}
    </div>
  );
}
