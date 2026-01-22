package bragdoc.infrastructure.integration.gemini;

import java.util.List;

/**
 * Modelo de resposta da API do Gemini.
 */
public record GeminiResponse(List<Candidate> candidates) {

    public record Candidate(Content content) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public String getGeneratedText() {
        if (candidates != null && !candidates.isEmpty() &&
                candidates.get(0).content() != null &&
                candidates.get(0).content().parts() != null &&
                !candidates.get(0).content().parts().isEmpty()) {
            return candidates.get(0).content().parts().get(0).text();
        }
        return "";
    }
}
