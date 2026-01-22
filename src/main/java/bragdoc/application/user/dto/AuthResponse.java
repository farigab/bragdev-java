package bragdoc.application.user.dto;

public record AuthResponse(
                String token,
                String refreshToken,
                UserResponse user) {
}
