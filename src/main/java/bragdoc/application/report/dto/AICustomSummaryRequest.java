package bragdoc.application.report.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AICustomSummaryRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

        @Size(max = 1000, message = "User prompt must not exceed 1000 characters") String userPrompt,

        List<String> repositories,

        String reportType,

        String category) {
    public AICustomSummaryRequest {
        if (reportType == null || reportType.isBlank()) {
            reportType = "executive";
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
    }
}
