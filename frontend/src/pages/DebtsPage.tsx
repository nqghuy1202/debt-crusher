import { useEffect, useMemo, useState } from "react";
import type { CSSProperties } from "react";
import { Link } from "react-router-dom";
import { ArrowDown, ArrowUp, ArrowUpDown, Pencil, Plus, Search, Trash2, TriangleAlert } from "lucide-react";
import { debtApi } from "../api/debtApi";
import type { DebtCategory, DebtRequest, DebtResponse } from "../api/types";
import { DebtForm } from "../components/DebtForm";
import { Modal } from "../components/Modal";
import { SummaryStrip } from "../components/SummaryStrip";
import { CATEGORY_BADGE_CLASS, DEBT_CATEGORIES } from "../utils/category";
import { useLanguage } from "../context/LanguageContext";

const currencyFormatter = new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" });

type SortKey = "name" | "balance" | "annualInterestRatePercent" | "minimumMonthlyPayment";
type SortDirection = "asc" | "desc";

export function DebtsPage() {
  const { t } = useLanguage();
  const columns: { key: SortKey; label: string }[] = [
    { key: "name", label: t("debts.col.name") },
    { key: "balance", label: t("debts.col.balance") },
    { key: "annualInterestRatePercent", label: t("debts.col.rate") },
    { key: "minimumMonthlyPayment", label: t("debts.col.minPayment") },
  ];

  const [debts, setDebts] = useState<DebtResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [editingDebt, setEditingDebt] = useState<DebtResponse | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [categoryFilter, setCategoryFilter] = useState<DebtCategory | "ALL">("ALL");
  const [sortKey, setSortKey] = useState<SortKey>("name");
  const [sortDirection, setSortDirection] = useState<SortDirection>("asc");

  const loadDebts = async () => {
    setIsLoading(true);
    setError(null);
    try {
      setDebts(await debtApi.list());
    } catch (err) {
      setError(err instanceof Error ? err.message : t("debts.loadError"));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDebts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleCreate = async (payload: DebtRequest) => {
    const created = await debtApi.create(payload);
    setDebts((prev) => [...prev, created]);
    setIsCreating(false);
  };

  const handleUpdate = async (debtId: number, payload: DebtRequest) => {
    const updated = await debtApi.update(debtId, payload);
    setDebts((prev) => prev.map((d) => (d.id === debtId ? updated : d)));
    setEditingDebt(null);
  };

  const handleDelete = async (debtId: number) => {
    if (!window.confirm(t("debts.deleteConfirm"))) return;
    try {
      await debtApi.remove(debtId);
      setDebts((prev) => prev.filter((d) => d.id !== debtId));
    } catch (err) {
      setError(err instanceof Error ? err.message : t("debts.deleteError"));
    }
  };

  const toggleSort = (key: SortKey) => {
    if (key === sortKey) {
      setSortDirection((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDirection("asc");
    }
  };

  const visibleDebts = useMemo(() => {
    const filtered = debts
      .filter((d) => d.name.toLowerCase().includes(searchTerm.trim().toLowerCase()))
      .filter((d) => categoryFilter === "ALL" || d.category === categoryFilter);
    const factor = sortDirection === "asc" ? 1 : -1;
    return [...filtered].sort((a, b) => {
      if (sortKey === "name") return a.name.localeCompare(b.name) * factor;
      return (a[sortKey] - b[sortKey]) * factor;
    });
  }, [debts, searchTerm, categoryFilter, sortKey, sortDirection]);

  const totalBalance = debts.reduce((sum, d) => sum + d.balance, 0);
  const totalMinPayment = debts.reduce((sum, d) => sum + d.minimumMonthlyPayment, 0);
  const isFiltered = searchTerm.trim() !== "" || categoryFilter !== "ALL";

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100">{t("debts.title")}</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">{t("debts.subtitle")}</p>
        </div>
        <div className="flex items-center gap-2">
          <Link
            to="/payoff/compare"
            className="inline-flex items-center gap-1.5 rounded-md border border-slate-300 px-3.5 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
          >
            {t("debts.compareLink")}
          </Link>
          <button
            onClick={() => setIsCreating(true)}
            className="inline-flex items-center gap-1.5 rounded-md bg-brand-500 px-3.5 py-2 text-sm font-semibold text-white transition hover:bg-brand-600"
          >
            <Plus size={16} />
            {t("debts.addButton")}
          </button>
        </div>
      </div>

      {error && (
        <p className="flex items-start gap-2 rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:border-rose-500/30 dark:bg-rose-500/10 dark:text-rose-400">
          <TriangleAlert size={16} className="mt-0.5 shrink-0" />
          {error}
        </p>
      )}

      <SummaryStrip
        items={[
          { label: t("debts.summary.totalBalance"), value: currencyFormatter.format(totalBalance) },
          { label: t("debts.summary.totalMinPayment"), value: currencyFormatter.format(totalMinPayment) },
          { label: t("debts.summary.count"), value: String(debts.length) },
        ]}
      />

      <div className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-white dark:border-slate-800 dark:bg-slate-900">
        <div className="flex flex-wrap items-center gap-2 border-b border-slate-200 p-3 dark:border-slate-800">
          <div className="relative min-w-[180px] flex-1">
            <Search size={15} className="pointer-events-none absolute top-1/2 left-2.5 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder={t("debts.searchPlaceholder")}
              className="w-full rounded-md border border-slate-300 bg-white py-1.5 pr-3 pl-8 text-sm text-slate-900 placeholder:text-slate-400 focus:border-brand-500 focus:ring-1 focus:ring-brand-500/30 focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
            />
          </div>
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value as DebtCategory | "ALL")}
            style={{ "--select-arrow-gap": "0.625rem" } as CSSProperties}
            className="select-arrow shrink-0 rounded-md border border-slate-300 bg-white py-1.5 pr-8 pl-2.5 text-sm text-slate-700 focus:border-brand-500 focus:ring-1 focus:ring-brand-500/30 focus:outline-none dark:border-slate-700 dark:bg-slate-950 dark:text-slate-300"
          >
            <option value="ALL">{t("debts.filterAll")}</option>
            {DEBT_CATEGORIES.map((category) => (
              <option key={category} value={category}>
                {t(`category.${category}`)}
              </option>
            ))}
          </select>
          {isFiltered && (
            <span className="shrink-0 text-xs text-slate-400">
              {t("debts.searchCount", { visible: visibleDebts.length, total: debts.length })}
            </span>
          )}
        </div>

        {isLoading ? (
          <p className="p-8 text-center text-sm text-slate-500 dark:text-slate-400">{t("debts.loading")}</p>
        ) : debts.length === 0 ? (
          <div className="flex flex-col items-center gap-2 p-10 text-center">
            <p className="text-sm text-slate-500 dark:text-slate-400">{t("debts.empty")}</p>
          </div>
        ) : (
          <div className="max-h-[28rem] overflow-auto">
            <table className="w-full min-w-[700px] text-sm">
              <thead className="sticky top-0 bg-slate-50 dark:bg-slate-900">
                <tr className="border-b border-slate-200 text-left text-xs font-medium tracking-wide text-slate-500 uppercase dark:border-slate-800 dark:text-slate-400">
                  {columns.map((col) => (
                    <th key={col.key} className="px-5 py-3">
                      <button
                        onClick={() => toggleSort(col.key)}
                        className="inline-flex items-center gap-1 transition hover:text-slate-700 dark:hover:text-slate-200"
                      >
                        {col.label}
                        {sortKey === col.key ? (
                          sortDirection === "asc" ? (
                            <ArrowUp size={12} />
                          ) : (
                            <ArrowDown size={12} />
                          )
                        ) : (
                          <ArrowUpDown size={12} className="opacity-40" />
                        )}
                      </button>
                    </th>
                  ))}
                  <th className="px-5 py-3">{t("debts.col.category")}</th>
                  <th className="px-5 py-3" />
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {visibleDebts.map((debt) => (
                  <tr key={debt.id} className="transition hover:bg-slate-50 dark:hover:bg-slate-800/50">
                    <td className="px-5 py-3 font-medium text-slate-900 dark:text-slate-100">{debt.name}</td>
                    <td className="px-5 py-3 tabular-nums text-slate-700 dark:text-slate-300">
                      {currencyFormatter.format(debt.balance)}
                    </td>
                    <td className="px-5 py-3 tabular-nums text-slate-700 dark:text-slate-300">
                      {debt.annualInterestRatePercent}%
                    </td>
                    <td className="px-5 py-3 tabular-nums text-slate-700 dark:text-slate-300">
                      {currencyFormatter.format(debt.minimumMonthlyPayment)}
                    </td>
                    <td className="px-5 py-3">
                      <span
                        className={`inline-flex items-center rounded-md border px-2 py-0.5 text-xs font-medium ${CATEGORY_BADGE_CLASS[debt.category]}`}
                      >
                        {t(`category.${debt.category}`)}
                      </span>
                    </td>
                    <td className="px-5 py-3">
                      <div className="flex justify-end gap-1">
                        <button
                          onClick={() => setEditingDebt(debt)}
                          aria-label={t("debts.editAria")}
                          title={t("debts.editAria")}
                          className="rounded-md p-1.5 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:hover:bg-slate-800 dark:hover:text-slate-200"
                        >
                          <Pencil size={15} />
                        </button>
                        <button
                          onClick={() => handleDelete(debt.id)}
                          aria-label={t("debts.deleteAria")}
                          title={t("debts.deleteAria")}
                          className="rounded-md p-1.5 text-slate-400 transition hover:bg-rose-50 hover:text-rose-600 dark:hover:bg-rose-500/10 dark:hover:text-rose-400"
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {visibleDebts.length === 0 && (
                  <tr>
                    <td colSpan={columns.length + 2} className="px-5 py-8 text-center text-sm text-slate-500 dark:text-slate-400">
                      {t("debts.noMatch", { term: searchTerm })}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {isCreating && (
        <Modal title={t("debts.modal.addTitle")} onClose={() => setIsCreating(false)}>
          <DebtForm submitLabel={t("debtForm.create")} onSubmit={handleCreate} onCancel={() => setIsCreating(false)} />
        </Modal>
      )}

      {editingDebt && (
        <Modal title={t("debts.modal.editTitle")} onClose={() => setEditingDebt(null)}>
          <DebtForm
            initialValue={editingDebt}
            submitLabel={t("debtForm.save")}
            onSubmit={(payload) => handleUpdate(editingDebt.id, payload)}
            onCancel={() => setEditingDebt(null)}
          />
        </Modal>
      )}
    </div>
  );
}
