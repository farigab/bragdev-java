package bragdoc.application.report;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bragdoc.application.report.dto.AIReportResponse;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.report.AIReportGenerator;
import bragdoc.domain.report.Report;
import bragdoc.domain.report.ReportFilter;
import bragdoc.domain.report.ReportType;

/**
 * Caso de uso: Gerar análise de contribuições do GitHub com IA.
 */
public class GenerateGitHubAnalysisUseCase {

    private final AchievementRepository achievementRepository;
    private final AIReportGenerator aiReportGenerator;

    public GenerateGitHubAnalysisUseCase(
            AchievementRepository achievementRepository,
            AIReportGenerator aiReportGenerator) {
        this.achievementRepository = achievementRepository;
        this.aiReportGenerator = aiReportGenerator;
    }

    public AIReportResponse execute(String userLogin) {
        // 1. Buscar apenas conquistas do GitHub
        List<Achievement> githubAchievements = achievementRepository.findByUser(userLogin)
                .stream()
                .filter(a -> a.getCategory().isGitHub())
                .toList();

        if (githubAchievements.isEmpty()) {
            return new AIReportResponse(
                    "github",
                    0,
                    "Nenhuma conquista do GitHub encontrada",
                    new AIReportResponse.FiltersApplied(
                            null,
                            null,
                            null));
        }

        // 2. Criar relatório específico de GitHub
        Report report = new Report(
                ReportType.GITHUB,
                githubAchievements,
                ReportFilter.empty());

        // 3. Preparar dados específicos do GitHub
        String enrichedData = prepareGitHubData(report);

        // 4. Gerar análise com IA
        String aiAnalysis = aiReportGenerator.generateReport(enrichedData, ReportType.GITHUB);

        // 5. Preparar estatísticas
        Map<String, Long> statistics = report.getStatistics().byCategory();

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("statistics", statistics);

        return new AIReportResponse(
                "github",
                githubAchievements.size(),
                aiAnalysis,
                new AIReportResponse.FiltersApplied(
                        null,
                        null,
                        null));
    }

    private String prepareGitHubData(Report report) {
        var data = new StringBuilder();
        var achievements = report.getAchievements();

        data.append("=== ANÁLISE DE CONTRIBUIÇÕES GITHUB ===\n\n");
        data.append("Total de contribuições: ").append(achievements.size()).append("\n");

        // Estatísticas por tipo
        var byType = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getValue(),
                        Collectors.counting()));

        data.append("\nDistribuição por tipo:\n");
        byType.forEach((type, count) -> data.append("  ").append(type).append(": ").append(count).append("\n"));

        // Análise temporal
        if (!achievements.isEmpty()) {
            var earliest = achievements.stream()
                    .map(Achievement::getDate)
                    .min(java.time.LocalDate::compareTo)
                    .orElse(null);

            var latest = achievements.stream()
                    .map(Achievement::getDate)
                    .max(java.time.LocalDate::compareTo)
                    .orElse(null);

            if (earliest != null && latest != null) {
                long months = java.time.temporal.ChronoUnit.MONTHS.between(earliest, latest);
                data.append("\nPeríodo de atividade: ").append(earliest)
                        .append(" até ").append(latest).append("\n");
                if (months > 0) {
                    double avgPerMonth = (double) achievements.size() / months;
                    data.append("Média de contribuições: ")
                            .append(String.format("%.1f", avgPerMonth))
                            .append(" por mês\n");
                }
            }
        }

        data.append("\n=== CONTRIBUIÇÕES DETALHADAS ===\n\n");

        byType.keySet().stream()
                .sorted()
                .forEach(type -> {
                    var items = achievements.stream()
                            .filter(a -> type.equals(a.getCategory().getValue()))
                            .sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate()))
                            .toList();

                    data.append("## ").append(type).append("\n\n");

                    items.forEach(a -> {
                        data.append("• **").append(a.getTitle()).append("**\n");
                        data.append("  Data: ").append(a.getDate()).append("\n");
                        if (a.getDescription() != null && !a.getDescription().isEmpty()) {
                            data.append("  Descrição: ").append(a.getDescription()).append("\n");
                        }
                        data.append("\n");
                    });
                });

        return data.toString();
    }
}
