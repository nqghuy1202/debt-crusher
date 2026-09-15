import { useLayoutEffect, useRef } from "react";
import type { ChangeEvent } from "react";

const formatter = new Intl.NumberFormat("vi-VN");

/** Đếm số ký tự số đứng trước vị trí `index` trong `str` — dùng để giữ đúng vị trí con trỏ khi format lại. */
function countDigitsBefore(str: string, index: number): number {
  let count = 0;
  for (let i = 0; i < index && i < str.length; i++) {
    if (/\d/.test(str[i])) count++;
  }
  return count;
}

/** Tìm vị trí trong `str` ngay sau khi đã gặp đủ `n` ký tự số. */
function indexAfterDigits(str: string, n: number): number {
  if (n <= 0) return 0;
  let count = 0;
  for (let i = 0; i < str.length; i++) {
    if (/\d/.test(str[i])) {
      count++;
      if (count === n) return i + 1;
    }
  }
  return str.length;
}

interface FormattedNumberInputProps {
  value: string;
  onChange: (rawDigits: string) => void;
  className?: string;
  placeholder?: string;
  required?: boolean;
  id?: string;
}

/**
 * Input số nguyên (VNĐ không dùng số lẻ) tự thêm dấu chấm phân tách hàng nghìn ngay
 * khi gõ (kiểu "18.000.000"), giữ nguyên vị trí con trỏ dựa trên số ký tự số đã gõ
 * trước đó — không nhảy về cuối như cách format-lại-toàn-bộ ngây thơ hay gặp.
 */
export function FormattedNumberInput({ value, onChange, className, placeholder, required, id }: FormattedNumberInputProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const pendingCursor = useRef<number | null>(null);

  const display = value === "" ? "" : formatter.format(Number(value));

  useLayoutEffect(() => {
    if (pendingCursor.current !== null && inputRef.current) {
      inputRef.current.setSelectionRange(pendingCursor.current, pendingCursor.current);
      pendingCursor.current = null;
    }
  }, [display]);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const cursorPos = e.target.selectionStart ?? e.target.value.length;
    const digitsBeforeCursor = countDigitsBefore(e.target.value, cursorPos);
    const rawDigits = e.target.value.replace(/\D/g, "").replace(/^0+(?=\d)/, "");

    const newDisplay = rawDigits === "" ? "" : formatter.format(Number(rawDigits));
    pendingCursor.current = indexAfterDigits(newDisplay, digitsBeforeCursor);

    onChange(rawDigits);
  };

  return (
    <input
      ref={inputRef}
      type="text"
      inputMode="numeric"
      id={id}
      value={display}
      onChange={handleChange}
      placeholder={placeholder}
      required={required}
      className={className}
    />
  );
}
