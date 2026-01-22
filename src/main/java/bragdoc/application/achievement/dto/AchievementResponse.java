package bragdoc.application.achievement.dto;

import java.time.LocalDate;

import bragdoc.domain.achievement.Achievement;

public record AchievementResponse(
        Long id,
        String title,
        String description,
        String category,
        LocalDate date) {
    public static AchievementResponse from(Achievement achievement) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getCategory().getValue(),
                achievement.getDate());
    }
}
