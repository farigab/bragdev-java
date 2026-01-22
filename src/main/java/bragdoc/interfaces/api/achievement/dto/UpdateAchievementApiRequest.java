package bragdoc.interfaces.api.achievement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAchievementApiRequest(
        @NotBlank(message = "Título é obrigatório") String title,

        String description,

        @NotBlank(message = "Categoria é obrigatória") String category,

        @NotNull(message = "Data é obrigatória") LocalDate date) {
}
