package bragdoc.application.user.dto;

import bragdoc.domain.user.User;

public record UserResponse(
        String login,
        String name,
        String avatarUrl,
        boolean hasGitHubToken) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getLogin(),
                user.getName(),
                user.getAvatarUrl(),
                user.hasGitHubToken());
    }
}
