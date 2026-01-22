package bragdoc.application.report;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bragdoc.application.report.dto.ReportResponse;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;

/**
 * Caso de uso: Gerar relatório por categoria.
 */
public class GenerateCategoryReportUseCase {

    private final AchievementRepository achievementRepository;

    public GenerateCategoryReportUseCase(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public ReportResponse execute(String category, String userLogin) {
        List<Achievement> achievements = category != null
                ? achievementRepository.findByUserAndCategory(userLogin, category)
                : achievementRepository.findByUser(userLogin);

        var byCategory = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getValue(),
                        Collectors.counting()));

        Map<String, Object> filters = Map.of(
                "category", category != null ? category : "all");

        return new ReportResponse(
                "by_category",
                achievements.size(),
                byCategory,
                filters);
    }
}
