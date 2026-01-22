package bragdoc.interfaces.api.user.dto;

import bragdoc.application.user.dto.UserResponse;

public record UserApiResponse(
        String login,
        String name,
        String avatarUrl,
        boolean hasGitHubToken) {
    public static UserApiResponse from(UserResponse response) {
        return new UserApiResponse(
                response.login(),
                response.name(),
                response.avatarUrl(),
                response.hasGitHubToken());
    }
}
