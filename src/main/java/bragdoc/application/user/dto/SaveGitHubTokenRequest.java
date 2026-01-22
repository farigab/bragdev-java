package bragdoc.application.user.dto;

public record SaveGitHubTokenRequest(String token) {
    public SaveGitHubTokenRequest {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token é obrigatório");
        }
    }
}
