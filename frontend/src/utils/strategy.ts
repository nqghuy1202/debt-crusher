import { CircleDashed, Mountain, Snowflake } from "lucide-react";
import type { ComponentType } from "react";
import type { PayoffStrategyType } from "../api/types";

type TranslateFn = (key: string) => string;

/**
 * Màu categorical slot 1/2/3 (blue/orange/aqua) từ bộ palette đã validate CVD —
 * xem dataviz skill. Cố định theo strategy, không đổi theo rank/giá trị/ngôn ngữ.
 */
const STRATEGY_COLOR: Record<PayoffStrategyType, string> = {
  AVALANCHE: "#2a78d6",
  SNOWBALL: "#eb6834",
  MINIMUM_ONLY: "#1baf7a",
};

const STRATEGY_ICON: Record<PayoffStrategyType, ComponentType<{ size?: number; className?: string }>> = {
  AVALANCHE: Mountain,
  SNOWBALL: Snowflake,
  MINIMUM_ONLY: CircleDashed,
};

/** Label/description phụ thuộc ngôn ngữ hiện tại, lấy qua t() từ LanguageContext. */
export function getStrategyMeta(t: TranslateFn) {
  const strategies: PayoffStrategyType[] = ["AVALANCHE", "SNOWBALL", "MINIMUM_ONLY"];
  return Object.fromEntries(
    strategies.map((strategy) => [
      strategy,
      {
        label: t(`strategy.${strategy}.label`),
        description: t(`strategy.${strategy}.description`),
        color: STRATEGY_COLOR[strategy],
        icon: STRATEGY_ICON[strategy],
      },
    ]),
  ) as Record<
    PayoffStrategyType,
    { label: string; description: string; color: string; icon: ComponentType<{ size?: number; className?: string }> }
  >;
}
