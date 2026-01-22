package bragdoc.application.achievement.dto;

import java.time.LocalDate;

public record UpdateAchievementRequest(
        String title,
        String description,
        String category,
        LocalDate date) {
}
