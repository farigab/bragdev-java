package bragdoc.domain.report;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bragdoc.domain.achievement.Achievement;

/**
 * Entidade de domínio representando um relatório gerado.
 */
public class Report {
    private final ReportType type;
    private final List<Achievement> achievements;
    private final ReportFilter filter;
    private final ReportStatistics statistics;

    public Report(ReportType type, List<Achievement> achievements, ReportFilter filter) {
        this.type = type;
        this.achievements = List.copyOf(achievements);
        this.filter = filter;
        this.statistics = calculateStatistics();
    }

    private ReportStatistics calculateStatistics() {
        int total = achievements.size();

        Map<String, Long> byCategory = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getValue(),
                        Collectors.counting()));

        return new ReportStatistics(total, byCategory);
    }

    public String prepareEnrichedData() {
        var data = new StringBuilder();

        data.append("=== CONTEXTO GERAL ===\n");
        data.append("Total de conquistas: ").append(statistics.total()).append("\n");
        data.append("Categorias distintas: ").append(statistics.byCategory().size()).append("\n");

        if (filter.hasDateRange()) {
            data.append("Período: ").append(filter.startDate())
                    .append(" até ").append(filter.endDate()).append("\n");
        }

        data.append("\n=== DISTRIBUIÇÃO POR CATEGORIA ===\n");
        statistics.byCategory().forEach((cat, count) -> data.append("  - ").append(cat).append(": ").append(count)
                .append(" (").append(String.format("%.1f", (count * 100.0 / statistics.total())))
                .append("% )\n"));

        data.append("\n=== CONQUISTAS DETALHADAS ===\n\n");

        statistics.byCategory().keySet().stream()
                .sorted()
                .forEach(category -> {
                    var items = achievements.stream()
                            .filter(a -> category.equals(a.getCategory().getValue()))
                            .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                            .toList();

                    data.append("## ").append(category).append(" (")
                            .append(items.size()).append(" conquistas)\n\n");

                    items.forEach(a -> {
                        data.append("**").append(a.getTitle()).append("**\n");
                        data.append("Data: ").append(a.getDate()).append("\n");
                        if (a.getDescription() != null && !a.getDescription().isEmpty()) {
                            data.append("Descrição: ").append(a.getDescription()).append("\n");
                        }
                        data.append("\n");
                    });
                });

        return data.toString();
    }

    public ReportType getType() {
        return type;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public ReportFilter getFilter() {
        return filter;
    }

    public ReportStatistics getStatistics() {
        return statistics;
    }
}
