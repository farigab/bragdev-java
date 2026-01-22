package bragdoc.interfaces.api.github.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ImportGitHubDataApiRequest(
        List<String> repositories,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}
