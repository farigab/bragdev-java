package bragdoc.application.report;

import java.util.ArrayList;
import java.util.List;

import bragdoc.application.report.dto.AIReportResponse;
import bragdoc.application.report.dto.GenerateReportRequest;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.github.GitHubClient;
import bragdoc.domain.github.GitHubContribution;
import bragdoc.domain.github.GitHubRepository;
import bragdoc.domain.github.ImportPeriod;
import bragdoc.domain.report.AIReportGenerator;
import bragdoc.domain.report.Report;
import bragdoc.domain.report.ReportFilter;
import bragdoc.domain.report.ReportType;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.user.UserRepository;

/**
 * Caso de uso: Gerar relatório com IA.
 */
public class GenerateAIReportUseCase {

    private final AchievementRepository achievementRepository;
    private final AIReportGenerator aiReportGenerator;
    private final UserRepository userRepository;
    private final GitHubClient gitHubClient;

    public GenerateAIReportUseCase(
            AchievementRepository achievementRepository,
            AIReportGenerator aiReportGenerator,
            UserRepository userRepository,
            GitHubClient gitHubClient) {
        this.achievementRepository = achievementRepository;
        this.aiReportGenerator = aiReportGenerator;
        this.userRepository = userRepository;
        this.gitHubClient = gitHubClient;
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

    /**
     * Variante customizada que permite passar um prompt do usuário e um repositório
     * que serão considerados na geração do prompt para a IA.
     */
    public AIReportResponse executeCustom(GenerateReportRequest request, String userLogin, String userPrompt,
            String repository) {
        ReportType reportType = ReportType.from(request.reportType());

        ReportFilter filter = buildFilter(request);

        List<Achievement> achievements;
        // Se foi informado um repositório, buscar diretamente do GitHub
        if (repository != null && !repository.isBlank()) {
            var user = userRepository.findByLogin(userLogin)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

            if (!user.hasGitHubToken()) {
                // Se não tem token, manter comportamento antigo (BD)
                achievements = fetchAchievements(filter, userLogin);
            } else {
                String accessToken = user.getGithubTokenValue();

                GitHubRepository ghRepo = GitHubRepository.from(repository);

                ImportPeriod period = ImportPeriod.all();
                if (filter.hasDateRange()) {
                    period = ImportPeriod.between(
                            filter.startDate().atStartOfDay(),
                            filter.endDate().atTime(23, 59, 59));
                }

                List<GitHubContribution> prs = gitHubClient.fetchPullRequests(ghRepo, accessToken, period);
                List<GitHubContribution> commits = gitHubClient.fetchCommits(ghRepo, accessToken, period);

                List<Achievement> converted = new ArrayList<>();
                prs.forEach(p -> converted.add(p.toAchievement(userLogin)));
                commits.forEach(c -> converted.add(c.toAchievement(userLogin)));

                achievements = converted;
            }
        } else {
            achievements = fetchAchievements(filter, userLogin);
        }

        Report report = new Report(reportType, achievements, filter);

        String enrichedData = report.prepareEnrichedData();

        String aiReport = aiReportGenerator.generateReport(enrichedData, reportType, userPrompt, repository);

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
