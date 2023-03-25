package uz.aim.zerikdim5.dtos.jwt;

public record JwtResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType) {
}
