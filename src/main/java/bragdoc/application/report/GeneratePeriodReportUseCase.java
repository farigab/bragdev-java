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
 * Caso de uso: Gerar relatório por período.
 */
public class GeneratePeriodReportUseCase {

    private final AchievementRepository achievementRepository;

    public GeneratePeriodReportUseCase(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public ReportResponse execute(LocalDate startDate, LocalDate endDate, String userLogin) {
        List<Achievement> achievements = achievementRepository
                .findByUserAndDateRange(userLogin, startDate, endDate);

        var byCategory = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory().getValue(),
                        Collectors.counting()));

        var byMonth = achievements.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDate().getYear() + "-"
                                + String.format("%02d", a.getDate().getMonthValue()),
                        Collectors.counting()));

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("start_date", startDate);
        filters.put("end_date", endDate);
        filters.put("by_month", byMonth);

        return new ReportResponse(
                "by_period",
                achievements.size(),
                byCategory,
                filters);
    }
}
