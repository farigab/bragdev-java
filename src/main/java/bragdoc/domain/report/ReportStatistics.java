package bragdoc.domain.report;

import java.util.Map;

/**
 * Value Object contendo estatísticas de um relatório.
 */
public record ReportStatistics(
        int total,
        Map<String, Long> byCategory) {
}
