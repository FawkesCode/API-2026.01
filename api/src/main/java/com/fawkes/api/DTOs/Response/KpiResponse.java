package com.fawkes.api.DTOs.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class KpiResponse {

    private String key;
    private String label;
    private Object value;
    private Object previousValue;
    private Double changePercent;
    private Trend trend;
    private String status;   // "ok" | "warning" | "critical"

    public enum Trend { UP, DOWN, STABLE }

    public static KpiResponse of(String key, String label,
                                  Number current, Number previous) {
        double curr = current.doubleValue();
        double prev = previous.doubleValue();

        double pct = prev == 0 ? 0 : ((curr - prev) / prev) * 100.0;
        Trend trend = pct > 0.5 ? Trend.UP : pct < -0.5 ? Trend.DOWN : Trend.STABLE;

        return KpiResponse.builder()
                .key(key)
                .label(label)
                .value(current)
                .previousValue(previous)
                .changePercent(Math.round(pct * 10.0) / 10.0)
                .trend(trend)
                .build();
    }
}
