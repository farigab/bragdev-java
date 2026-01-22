package bragdoc.application.report.dto;

import java.time.LocalDate;

public record GenerateReportRequest(
        String reportType,
        String category,
        LocalDate startDate,
        LocalDate endDate) {
}
