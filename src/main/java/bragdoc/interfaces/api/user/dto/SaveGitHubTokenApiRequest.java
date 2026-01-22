package bragdoc.interfaces.api.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SaveGitHubTokenApiRequest(
        @NotBlank(message = "Token é obrigatório") String token) {
}
