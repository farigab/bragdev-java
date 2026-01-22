package bragdoc.application.github;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import bragdoc.application.github.dto.ImportGitHubDataRequest;
import bragdoc.application.github.dto.ImportGitHubDataResponse;
import bragdoc.domain.achievement.Achievement;
import bragdoc.domain.achievement.AchievementRepository;
import bragdoc.domain.github.GitHubClient;
import bragdoc.domain.github.GitHubContribution;
import bragdoc.domain.github.GitHubRepository;
import bragdoc.domain.github.ImportPeriod;
import bragdoc.domain.github.ImportResult;
import bragdoc.domain.shared.exceptions.EntityNotFoundException;
import bragdoc.domain.shared.exceptions.UnauthorizedException;
import bragdoc.domain.user.User;
import bragdoc.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de uso: Importar dados do GitHub e criar achievements.
 */
@Slf4j
public class ImportGitHubDataUseCase {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final GitHubClient gitHubClient;

    public ImportGitHubDataUseCase(
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            GitHubClient gitHubClient) {
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.gitHubClient = gitHubClient;
    }

    public ImportGitHubDataResponse execute(ImportGitHubDataRequest request, String userLogin) {
        // 1. Buscar usuário e validar token
        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (!user.hasGitHubToken()) {
            throw new UnauthorizedException("Token do GitHub não configurado");
        }

        String accessToken = user.getGithubTokenValue();

        // 2. Determinar período de importação
        ImportPeriod period = request.dataInicio() != null && request.dataFim() != null
                ? ImportPeriod.between(request.dataInicio(), request.dataFim())
                : ImportPeriod.all();

        // 3. Determinar repositórios alvo
        List<GitHubRepository> targetRepositories = resolveTargetRepositories(
                request.repositories(),
                accessToken);

        // 4. Importar dados de cada repositório em paralelo
        List<CompletableFuture<RepositoryImportResult>> futures = targetRepositories.stream()
                .map(repo -> CompletableFuture
                        .supplyAsync(() -> importFromRepository(repo, accessToken, period, userLogin)))
                .toList();

        // 5. Aguardar todas as importações
        List<RepositoryImportResult> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // 6. Consolidar resultados
        ImportResult importResult = consolidateResults(results, targetRepositories.size());

        log.info("Importação concluída: {} repos, {} PRs, {} commits, {} issues",
                targetRepositories.size(), importResult.pullRequests(),
                importResult.commits(), importResult.issues());

        return ImportGitHubDataResponse.from(importResult);
    }

    private List<GitHubRepository> resolveTargetRepositories(
            List<String> requestedRepos,
            String accessToken) {

        if (requestedRepos == null || requestedRepos.isEmpty()) {
            // Se não especificado, importa todos os repositórios
            return gitHubClient.listRepositories(accessToken);
        }

        // Converte lista de nomes em objetos GitHubRepository
        return requestedRepos.stream()
                .map(name -> {
                    if (name.contains("/")) {
                        return GitHubRepository.from(name);
                    }
                    // Se não tem '/', assume que é nome sem owner
                    // Busca owner dos repositórios disponíveis
                    var allRepos = gitHubClient.listRepositories(accessToken);
                    return allRepos.stream()
                            .filter(r -> r.name().equals(name))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Repositório não encontrado: " + name));
                })
                .toList();
    }

    private RepositoryImportResult importFromRepository(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period,
            String userLogin) {
        try {
            log.info("Importando dados do repositório: {}", repository.fullName());

            // Buscar contribuições
            List<GitHubContribution> pullRequests = gitHubClient.fetchPullRequests(
                    repository, accessToken, period);

            List<GitHubContribution> commits = gitHubClient.fetchCommits(
                    repository, accessToken, period);

            List<GitHubContribution> issues = gitHubClient.fetchIssues(
                    repository, accessToken, period);

            // Converter em achievements e salvar
            int savedPRs = saveContributions(pullRequests, userLogin);
            int savedCommits = saveContributions(commits, userLogin);
            int savedIssues = saveContributions(issues, userLogin);

            return new RepositoryImportResult(savedPRs, savedIssues, savedCommits);

        } catch (Exception e) {
            log.error("Erro ao importar repositório {}: {}",
                    repository.fullName(), e.getMessage());
            return new RepositoryImportResult(0, 0, 0);
        }
    }

    private int saveContributions(List<GitHubContribution> contributions, String userLogin) {
        int saved = 0;

        for (GitHubContribution contribution : contributions) {
            Achievement achievement = contribution.toAchievement(userLogin);

            // Evitar duplicatas
            boolean exists = achievementRepository.existsByUserAndDateAndTitle(
                    userLogin,
                    achievement.getDate(),
                    achievement.getTitle());

            if (!exists) {
                achievementRepository.save(achievement);
                saved++;
            }
        }

        return saved;
    }

    private ImportResult consolidateResults(
            List<RepositoryImportResult> results,
            int totalRepos) {

        int totalPRs = results.stream().mapToInt(RepositoryImportResult::pullRequests).sum();
        int totalIssues = results.stream().mapToInt(RepositoryImportResult::issues).sum();
        int totalCommits = results.stream().mapToInt(RepositoryImportResult::commits).sum();

        return ImportResult.success(totalPRs, totalIssues, totalCommits, totalRepos);
    }

    /**
     * Record auxiliar para resultados de importação por repositório.
     */
    private record RepositoryImportResult(int pullRequests, int issues, int commits) {
    }
}
