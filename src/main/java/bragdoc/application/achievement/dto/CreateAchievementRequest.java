package bragdoc.application.achievement.dto;

import java.time.LocalDate;

public record CreateAchievementRequest(
        String title,
        String description,
        String category,
        LocalDate date) {
}
