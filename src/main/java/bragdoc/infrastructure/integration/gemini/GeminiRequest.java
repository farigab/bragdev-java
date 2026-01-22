package bragdoc.infrastructure.integration.gemini;

import java.util.List;

/**
 * Modelo de requisição para a API do Gemini.
 */
public record GeminiRequest(List<Content> contents) {

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public static GeminiRequest create(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
        return new GeminiRequest(List.of(content));
    }
}
