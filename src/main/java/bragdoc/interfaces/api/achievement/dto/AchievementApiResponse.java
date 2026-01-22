package bragdoc.interfaces.api.achievement.dto;

import java.time.LocalDate;

import bragdoc.application.achievement.dto.AchievementResponse;

public record AchievementApiResponse(
        Long id,
        String title,
        String description,
        String category,
        LocalDate date) {
    public static AchievementApiResponse from(AchievementResponse response) {
        return new AchievementApiResponse(
                response.id(),
                response.title(),
                response.description(),
                response.category(),
                response.date());
    }
}
