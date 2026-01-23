package bragdoc.interfaces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import bragdoc.application.achievement.CreateAchievementUseCase;
import bragdoc.application.achievement.DeleteAchievementUseCase;
import bragdoc.application.achievement.FindAchievementUseCase;
import bragdoc.application.achievement.UpdateAchievementUseCase;
import bragdoc.application.github.ImportGitHubDataUseCase;
import bragdoc.application.github.ListRepositoriesUseCase;
import bragdoc.application.report.GenerateAIReportUseCase;
import bragdoc.application.report.GenerateCategoryReportUseCase;
import bragdoc.application.report.GenerateGitHubAnalysisUseCase;
import bragdoc.application.report.GeneratePeriodReportUseCase;
import bragdoc.application.report.GenerateSummaryReportUseCase;
import bragdoc.application.user.AuthenticateUserUseCase;
import bragdoc.application.user.ClearGitHubTokenUseCase;
import bragdoc.application.user.GetCurrentUserUseCase;
import bragdoc.application.user.RefreshAccessTokenUseCase;
import bragdoc.application.user.RevokeAllRefreshTokensUseCase;
import bragdoc.application.user.RevokeRefreshTokenUseCase;
import bragdoc.application.user.SaveGitHubTokenUseCase;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.github.GitHubClient;
import bragdoc.domain.report.AIReportGenerator;
import bragdoc.domain.user.AuthTokenService;
import bragdoc.domain.user.OAuthService;
import bragdoc.domain.user.RefreshTokenRepository;
import bragdoc.domain.user.UserRepository;

/**
 * Configuração de beans da aplicação.
 * Aqui é onde fazemos a injeção de dependências e composição dos Use Cases.
 */
@Configuration
public class BeanConfiguration {

    // ============= ACHIEVEMENT USE CASES =============

    @Bean
    public CreateAchievementUseCase createAchievementUseCase(
            AchievementRepository repository) {
        return new CreateAchievementUseCase(repository);
    }

    @Bean
    public UpdateAchievementUseCase updateAchievementUseCase(
            AchievementRepository repository) {
        return new UpdateAchievementUseCase(repository);
    }

    @Bean
    public DeleteAchievementUseCase deleteAchievementUseCase(
            AchievementRepository repository) {
        return new DeleteAchievementUseCase(repository);
    }

    @Bean
    public FindAchievementUseCase findAchievementUseCase(
            AchievementRepository repository) {
        return new FindAchievementUseCase(repository);
    }

    // ============= USER USE CASES =============

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            OAuthService oauthService,
            UserRepository userRepository,
            AuthTokenService tokenService,
            RefreshTokenRepository refreshTokenRepository) {
        return new AuthenticateUserUseCase(
                oauthService,
                userRepository,
                tokenService,
                refreshTokenRepository);
    }

    @Bean
    public RefreshAccessTokenUseCase refreshAccessTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            AuthTokenService authTokenService) {
        return new RefreshAccessTokenUseCase(
                refreshTokenRepository,
                userRepository,
                authTokenService);
    }

    @Bean
    public RevokeRefreshTokenUseCase revokeRefreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository) {
        return new RevokeRefreshTokenUseCase(refreshTokenRepository);
    }

    @Bean
    public RevokeAllRefreshTokensUseCase revokeAllRefreshTokensUseCase(
            RefreshTokenRepository refreshTokenRepository) {
        return new RevokeAllRefreshTokensUseCase(refreshTokenRepository);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(
            UserRepository userRepository) {
        return new GetCurrentUserUseCase(userRepository);
    }

    @Bean
    public SaveGitHubTokenUseCase saveGitHubTokenUseCase(
            UserRepository userRepository) {
        return new SaveGitHubTokenUseCase(userRepository);
    }

    @Bean
    public ClearGitHubTokenUseCase clearGitHubTokenUseCase(
            UserRepository userRepository) {
        return new ClearGitHubTokenUseCase(userRepository);
    }

    // ============= REPORT USE CASES =============

    @Bean
    public GenerateSummaryReportUseCase generateSummaryReportUseCase(
            AchievementRepository achievementRepository) {
        return new GenerateSummaryReportUseCase(achievementRepository);
    }

    @Bean
    public GenerateCategoryReportUseCase generateCategoryReportUseCase(
            AchievementRepository achievementRepository) {
        return new GenerateCategoryReportUseCase(achievementRepository);
    }

    @Bean
    public GeneratePeriodReportUseCase generatePeriodReportUseCase(
            AchievementRepository achievementRepository) {
        return new GeneratePeriodReportUseCase(achievementRepository);
    }

    @Bean
    public GenerateAIReportUseCase generateAIReportUseCase(
            AchievementRepository achievementRepository,
            AIReportGenerator aiReportGenerator,
            UserRepository userRepository,
            GitHubClient gitHubClient) {
        return new GenerateAIReportUseCase(achievementRepository, aiReportGenerator, userRepository, gitHubClient);
    }

    @Bean
    public GenerateGitHubAnalysisUseCase generateGitHubAnalysisUseCase(
            AchievementRepository achievementRepository,
            AIReportGenerator aiReportGenerator) {
        return new GenerateGitHubAnalysisUseCase(achievementRepository, aiReportGenerator);
    }

    // ============= GITHUB USE CASES =============

    @Bean
    public ListRepositoriesUseCase listRepositoriesUseCase(
            UserRepository userRepository,
            GitHubClient gitHubClient) {
        return new ListRepositoriesUseCase(userRepository, gitHubClient);
    }

    @Bean
    public ImportGitHubDataUseCase importGitHubDataUseCase(
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            GitHubClient gitHubClient) {
        return new ImportGitHubDataUseCase(userRepository, achievementRepository, gitHubClient);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
