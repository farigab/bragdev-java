package bragdoc.domain.github;

/**
 * Enum representando os tipos de contribuição do GitHub.
 */
public enum GitHubContributionType {
    PULL_REQUEST("GitHub - Pull Request"),
    ISSUE("GitHub - Issue"),
    COMMIT("GitHub - Commit"),
    REPOSITORY("GitHub - Repository");

    private final String categoryName;

    GitHubContributionType(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
