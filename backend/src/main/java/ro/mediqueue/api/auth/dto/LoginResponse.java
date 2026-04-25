package ro.mediqueue.api.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresInMs,
        long userId,
        long clinicId,
        String role
) {}
