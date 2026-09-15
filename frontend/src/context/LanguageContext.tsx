import { createContext, useContext, useMemo, useState } from "react";
import type { ReactNode } from "react";
import type { Language } from "../i18n/translations";
import { translations } from "../i18n/translations";

interface LanguageState {
  language: Language;
  setLanguage: (language: Language) => void;
  toggleLanguage: () => void;
  t: (key: string, params?: Record<string, string | number>) => string;
}

const LanguageContext = createContext<LanguageState | undefined>(undefined);

function getInitialLanguage(): Language {
  const stored = localStorage.getItem("language");
  return stored === "en" ? "en" : "vi";
}

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>(getInitialLanguage);

  const setLanguage = (next: Language) => {
    localStorage.setItem("language", next);
    setLanguageState(next);
  };

  const value = useMemo<LanguageState>(
    () => ({
      language,
      setLanguage,
      toggleLanguage: () => setLanguage(language === "vi" ? "en" : "vi"),
      t: (key, params) => {
        let str = translations[language][key] ?? translations.vi[key] ?? key;
        if (params) {
          for (const [name, val] of Object.entries(params)) {
            str = str.replaceAll(`{${name}}`, String(val));
          }
        }
        return str;
      },
    }),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [language],
  );

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>;
}

export function useLanguage() {
  const ctx = useContext(LanguageContext);
  if (!ctx) {
    throw new Error("useLanguage phải được dùng bên trong LanguageProvider");
  }
  return ctx;
}
