package bragdoc.application.report.dto;

import java.time.LocalDate;

public record AIReportResponse(
                String reportType,
                int totalAchievements,
                String aiGeneratedReport,
                FiltersApplied filtersApplied) {

        public record FiltersApplied(
                        String category,
                        LocalDate startDate,
                        LocalDate endDate) {
        }
}
