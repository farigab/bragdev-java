package bragdoc.domain.github;

import java.time.LocalDateTime;

/**
 * Value Object representando o período de importação.
 */
public record ImportPeriod(LocalDateTime startDate, LocalDateTime endDate) {

    public ImportPeriod {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
    }

    public static ImportPeriod all() {
        return new ImportPeriod(null, null);
    }

    public static ImportPeriod between(LocalDateTime start, LocalDateTime end) {
        return new ImportPeriod(start, end);
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }
}
