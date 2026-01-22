package bragdoc.domain.github;

import java.time.LocalDate;

import bragdoc.domain.achievement.Achievement;

/**
 * Value Object representando uma contribuição do GitHub
 * que será convertida em Achievement.
 */
public record GitHubContribution(
        String title,
        String description,
        LocalDate date,
        GitHubContributionType type) {
    public Achievement toAchievement(String userLogin) {
        return Achievement.create(
                title,
                description,
                type.getCategoryName(),
                date,
                userLogin);
    }
}
