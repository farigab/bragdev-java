package bragdoc.domain.github;

/**
 * Value Object representando o resultado de uma importação.
 */
public record ImportResult(
        int pullRequests,
        int issues,
        int commits,
        int repositories,
        String message) {
    public static ImportResult success(int prs, int issues, int commits, int repos) {
        return new ImportResult(prs, issues, commits, repos, "Importação concluída com sucesso");
    }

    public static ImportResult empty(String message) {
        return new ImportResult(0, 0, 0, 0, message);
    }

    public int totalItems() {
        return pullRequests + issues + commits;
    }
}
