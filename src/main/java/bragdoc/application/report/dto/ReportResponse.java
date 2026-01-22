package bragdoc.application.report.dto;

import java.util.Map;

public record ReportResponse(
        String reportType,
        int totalAchievements,
        Map<String, Long> byCategory,
        Map<String, Object> filtersApplied) {
}
