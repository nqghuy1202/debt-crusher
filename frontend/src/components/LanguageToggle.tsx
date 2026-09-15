import { Languages } from "lucide-react";
import { useLanguage } from "../context/LanguageContext";

export function LanguageToggle() {
  const { language, toggleLanguage, t } = useLanguage();

  return (
    <button
      type="button"
      onClick={toggleLanguage}
      aria-label={t("language.switch")}
      title={t("language.switch")}
      className="inline-flex h-8 items-center gap-1.5 rounded-md border border-slate-300 px-2 text-xs font-semibold text-slate-500 transition hover:bg-slate-100 hover:text-slate-800 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-slate-200"
    >
      <Languages size={14} />
      {language.toUpperCase()}
    </button>
  );
}
