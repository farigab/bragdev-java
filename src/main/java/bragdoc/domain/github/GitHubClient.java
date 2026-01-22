package bragdoc.domain.github;

import java.util.List;

/**
 * Interface de serviço de domínio para integração com GitHub.
 * Implementação fica na infraestrutura.
 */
public interface GitHubClient {

    /**
     * Lista todos os repositórios do usuário.
     */
    List<GitHubRepository> listRepositories(String accessToken);

    /**
     * Busca pull requests de um repositório.
     */
    List<GitHubContribution> fetchPullRequests(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period);

    /**
     * Busca commits de um repositório.
     */
    List<GitHubContribution> fetchCommits(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period);

    /**
     * Busca issues de um repositório.
     */
    List<GitHubContribution> fetchIssues(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period);
}
