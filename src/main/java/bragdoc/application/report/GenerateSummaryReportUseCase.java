package bragdoc.application.report;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bragdoc.application.report.dto.ReportResponse;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;

/**
 * Caso de uso: Gerar relatório resumido de conquistas.
 */
public class GenerateSummaryReportUseCase {

    private final AchievementRepository achievementRepository;

    public GenerateSummaryReportUseCase(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public ReportResponse execute(String userLogin) {
        List<Achievement> achievements = achievementRepository.findByUser(userLogin);

        var byCategory = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getValue(),
                        Collectors.counting()));

        Map<String, Object> additionalInfo = new LinkedHashMap<>();
        additionalInfo.put("categories_count", byCategory.size());

        if (!achievements.isEmpty()) {
            LocalDate earliest = achievements.stream()
                    .map(Achievement::getDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            LocalDate latest = achievements.stream()
                    .map(Achievement::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            additionalInfo.put("earliest_date", earliest);
            additionalInfo.put("latest_date", latest);
        }

        return new ReportResponse(
                "summary",
                achievements.size(),
                byCategory,
                additionalInfo);
    }
}
