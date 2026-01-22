package bragdoc.application.report;

import java.util.List;

import bragdoc.application.report.dto.AIReportResponse;
import bragdoc.application.report.dto.GenerateReportRequest;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.report.AIReportGenerator;
import bragdoc.domain.report.Report;
import bragdoc.domain.report.ReportFilter;
import bragdoc.domain.report.ReportType;

/**
 * Caso de uso: Gerar relatório com IA.
 */
public class GenerateAIReportUseCase {

    private final AchievementRepository achievementRepository;
    private final AIReportGenerator aiReportGenerator;

    public GenerateAIReportUseCase(
            AchievementRepository achievementRepository,
            AIReportGenerator aiReportGenerator) {
        this.achievementRepository = achievementRepository;
        this.aiReportGenerator = aiReportGenerator;
    }

    public AIReportResponse execute(GenerateReportRequest request, String userLogin) {
        ReportType reportType = ReportType.from(request.reportType());

        ReportFilter filter = buildFilter(request);

        List<Achievement> achievements = fetchAchievements(filter, userLogin);

        Report report = new Report(reportType, achievements, filter);

        String enrichedData = report.prepareEnrichedData();

        String aiReport = aiReportGenerator.generateReport(enrichedData, reportType);

        AIReportResponse.FiltersApplied filtersApplied = new AIReportResponse.FiltersApplied(
                filter.category() != null ? filter.category() : "all",
                filter.startDate(),
                filter.endDate());

        return new AIReportResponse(
                reportType.getValue(),
                achievements.size(),
                aiReport,
                filtersApplied);
    }

    private ReportFilter buildFilter(GenerateReportRequest request) {
        if (request.category() != null) {
            return ReportFilter.withCategory(request.category());
        } else if (request.startDate() != null && request.endDate() != null) {
            return ReportFilter.withDateRange(request.startDate(), request.endDate());
        }
        return ReportFilter.empty();
    }

    private List<Achievement> fetchAchievements(ReportFilter filter, String userLogin) {
        if (filter.hasCategory()) {
            return achievementRepository.findByUserAndCategory(userLogin, filter.category());
        } else if (filter.hasDateRange()) {
            return achievementRepository.findByUserAndDateRange(
                    userLogin, filter.startDate(), filter.endDate());
        }
        return achievementRepository.findByUser(userLogin);
    }
}
