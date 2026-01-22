// ============= GITHUB MODULE - INFRASTRUCTURE LAYER =============

// infrastructure/integration/github/GitHubApiClient.java
package bragdoc.infrastructure.integration.github;

import bragdoc.domain.github.*;
import bragdoc.domain.github.GitHubClient;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do cliente GitHub usando a biblioteca kohsuke/github-api.
 */
@Service
@Slf4j
public class GitHubApiClient implements GitHubClient {

    @Override
    public List<GitHubRepository> listRepositories(String accessToken) {
        try {
            GitHub github = connectWithToken(accessToken);
            GHUser myself = github.getMyself();

            List<GitHubRepository> repositories = new ArrayList<>();

            for (GHRepository repo : myself.listRepositories()) {
                repositories.add(GitHubRepository.of(
                        repo.getOwnerName(),
                        repo.getName()));
            }

            return repositories;

        } catch (Exception e) {
            log.error("Erro ao listar repositórios: {}", e.getMessage());
            throw new RuntimeException("Falha ao listar repositórios do GitHub", e);
        }
    }

    @Override
    public List<GitHubContribution> fetchPullRequests(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period) {
        try {
            GitHub github = connectWithToken(accessToken);
            GHRepository ghRepo = github.getRepository(repository.fullName());
            String username = github.getMyself().getLogin();

            List<GitHubContribution> contributions = new ArrayList<>();

            for (GHPullRequest pr : ghRepo.getPullRequests(GHIssueState.CLOSED)) {
                if (pr.isMerged() && isUserAuthor(pr.getUser(), username)) {
                    LocalDate mergedDate = convertToLocalDate(pr.getMergedAt());

                    if (isWithinPeriod(mergedDate, period)) {
                        contributions.add(new GitHubContribution(
                                "PR Merged: " + pr.getTitle(),
                                "Pull Request #" + pr.getNumber() + " merged in " + repository.fullName(),
                                mergedDate,
                                GitHubContributionType.PULL_REQUEST));
                    }
                }
            }

            return contributions;

        } catch (Exception e) {
            log.error("Erro ao buscar PRs do repositório {}: {}",
                    repository.fullName(), e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<GitHubContribution> fetchCommits(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period) {
        try {
            GitHub github = connectWithToken(accessToken);
            GHRepository ghRepo = github.getRepository(repository.fullName());
            String myName = github.getMyself().getName();

            List<GitHubContribution> contributions = new ArrayList<>();

            List<GHCommit> commits = ghRepo.listCommits()
                    .iterator()
                    .nextPage();

            for (GHCommit commit : commits) {
                GitCommit commitInfo = commit.getCommitShortInfo();

                String authorName = commitInfo.getAuthor().getName();

                // Workaround para pegar o nome do autor do commit
                if (myName.contains(authorName)) {
                    LocalDate commitDate = convertToLocalDate(commit.getCommitDate());

                    if (isWithinPeriod(commitDate, period)) {
                        String message = commitInfo.getMessage();

                        contributions.add(new GitHubContribution(
                                "Commit: " + truncate(message, 100),
                                "Commit in " + repository.fullName(),
                                commitDate,
                                GitHubContributionType.COMMIT));
                    }
                }
            }

            return contributions;

        } catch (Exception e) {
            log.error("Erro ao buscar commits do repositório {}: {}",
                    repository.fullName(), e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<GitHubContribution> fetchIssues(
            GitHubRepository repository,
            String accessToken,
            ImportPeriod period) {
        try {
            GitHub github = connectWithToken(accessToken);
            GHRepository ghRepo = github.getRepository(repository.fullName());
            String username = github.getMyself().getLogin();

            List<GitHubContribution> contributions = new ArrayList<>();

            for (GHIssue issue : ghRepo.getIssues(GHIssueState.CLOSED)) {
                if (isUserAuthor(issue.getUser(), username)) {
                    LocalDate closedDate = convertToLocalDate(issue.getClosedAt());

                    if (closedDate != null && isWithinPeriod(closedDate, period)) {
                        contributions.add(new GitHubContribution(
                                "Issue Closed: " + issue.getTitle(),
                                "Issue #" + issue.getNumber() + " in " + repository.fullName(),
                                closedDate,
                                GitHubContributionType.ISSUE));
                    }
                }
            }

            return contributions;

        } catch (Exception e) {
            log.error("Erro ao buscar issues do repositório {}: {}",
                    repository.fullName(), e.getMessage());
            return List.of();
        }
    }

    private GitHub connectWithToken(String accessToken) throws Exception {
        return new GitHubBuilder()
                .withOAuthToken(accessToken)
                .build();
    }

    private boolean isUserAuthor(GHUser author, String targetUsername) throws Exception {
        return author != null && targetUsername.equals(author.getLogin());
    }

    private LocalDate convertToLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private boolean isWithinPeriod(LocalDate date, ImportPeriod period) {
        if (!period.hasDateRange() || date == null) {
            return true; // Sem filtro de período
        }

        LocalDate start = period.startDate().toLocalDate();
        LocalDate end = period.endDate().toLocalDate();

        return !date.isBefore(start) && !date.isAfter(end);
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength
                ? text.substring(0, maxLength) + "..."
                : text;
    }
}
