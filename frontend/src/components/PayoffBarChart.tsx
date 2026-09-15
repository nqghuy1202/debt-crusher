import type { PayoffProjectionResponse } from "../api/types";
import { getStrategyMeta } from "../utils/strategy";
import { useLanguage } from "../context/LanguageContext";

const currencyFormatter = new Intl.NumberFormat("vi-VN", {
  style: "currency",
  currency: "VND",
  maximumFractionDigits: 0,
});

interface PayoffBarChartProps {
  projections: PayoffProjectionResponse[];
}

/** So sánh tổng tiền lãi phải trả theo từng chiến lược — insight chính của màn hình. */
export function PayoffBarChart({ projections }: PayoffBarChartProps) {
  const { t } = useLanguage();
  const strategyMeta = getStrategyMeta(t);
  const maxInterest = Math.max(...projections.map((p) => p.totalInterestPaid), 1);

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-800 dark:bg-slate-900">
      <div className="mb-5 flex flex-wrap gap-4 text-sm text-slate-500 dark:text-slate-400">
        {projections.map((p) => (
          <span key={p.strategy} className="inline-flex items-center gap-1.5">
            <span className="h-2.5 w-2.5 rounded-full" style={{ background: strategyMeta[p.strategy].color }} />
            {strategyMeta[p.strategy].label}
          </span>
        ))}
      </div>
      <div className="flex flex-col gap-3.5">
        {projections.map((p) => {
          const widthPercent = (p.totalInterestPaid / maxInterest) * 100;
          return (
            <div key={p.strategy} className="flex flex-col gap-1.5 sm:grid sm:grid-cols-[110px_1fr_auto] sm:items-center sm:gap-3">
              <div className="flex items-center justify-between sm:contents">
                <span className="text-sm text-slate-500 dark:text-slate-400">{strategyMeta[p.strategy].label}</span>
                <span className="text-sm tabular-nums whitespace-nowrap text-slate-700 sm:order-3 dark:text-slate-300">
                  {currencyFormatter.format(p.totalInterestPaid)}
                </span>
              </div>
              <div className="h-6 overflow-hidden rounded-md bg-slate-100 dark:bg-slate-800">
                <div
                  className="h-full min-w-1 rounded-md transition-[width] duration-300 ease-out"
                  style={{ width: `${widthPercent}%`, background: strategyMeta[p.strategy].color }}
                  title={`${strategyMeta[p.strategy].label}: ${currencyFormatter.format(p.totalInterestPaid)}`}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
