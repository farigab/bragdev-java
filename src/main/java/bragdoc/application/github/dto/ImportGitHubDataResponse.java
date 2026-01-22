package bragdoc.application.github.dto;

import bragdoc.domain.github.ImportResult;

public record ImportGitHubDataResponse(
        Integer pullRequests,
        Integer issues,
        Integer commits,
        Integer repositories,
        String message) {
    public static ImportGitHubDataResponse from(ImportResult result) {
        return new ImportGitHubDataResponse(
                result.pullRequests(),
                result.issues(),
                result.commits(),
                result.repositories(),
                result.message());
    }
}
