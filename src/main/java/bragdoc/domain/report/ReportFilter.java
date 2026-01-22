package bragdoc.domain.report;

import java.time.LocalDate;

/**
 * Value Object representando filtros para geração de relatórios.
 */
public record ReportFilter(
        String category,
        LocalDate startDate,
        LocalDate endDate) {
    public boolean hasCategory() {
        return category != null && !category.isBlank();
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    public static ReportFilter empty() {
        return new ReportFilter(null, null, null);
    }

    public static ReportFilter withCategory(String category) {
        return new ReportFilter(category, null, null);
    }

    public static ReportFilter withDateRange(LocalDate startDate, LocalDate endDate) {
        return new ReportFilter(null, startDate, endDate);
    }
}
