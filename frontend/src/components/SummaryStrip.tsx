interface SummaryItem {
  label: string;
  value: string;
}

/**
 * Dải chỉ số tổng hợp dạng bảng — nhãn nhỏ + số liệu monospace, ngăn bằng
 * đường viền mảnh thay vì các card tròn nổi riêng lẻ. Kiểu Mercury/Linear.
 */
export function SummaryStrip({ items }: { items: SummaryItem[] }) {
  return (
    <div className="grid grid-cols-1 divide-y divide-slate-200 rounded-lg border border-slate-200 bg-white sm:grid-cols-3 sm:divide-x sm:divide-y-0 dark:divide-slate-800 dark:border-slate-800 dark:bg-slate-900">
      {items.map((item) => (
        <div key={item.label} className="px-5 py-4">
          <p className="text-xs font-medium tracking-wide text-slate-500 uppercase dark:text-slate-400">{item.label}</p>
          <p className="mt-1 text-xl font-semibold tabular-nums text-slate-900 dark:text-slate-100">
            {item.value}
          </p>
        </div>
      ))}
    </div>
  );
}
