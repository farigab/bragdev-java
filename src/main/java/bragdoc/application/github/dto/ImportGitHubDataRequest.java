package bragdoc.application.github.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ImportGitHubDataRequest(
        List<String> repositories,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}
