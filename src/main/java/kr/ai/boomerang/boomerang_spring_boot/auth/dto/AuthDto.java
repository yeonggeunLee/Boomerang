package kr.ai.boomerang.boomerang_spring_boot.auth.dto;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;

/**
 * 인증 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class AuthDto {

    /**
     * JWT 토큰 응답 DTO
     */
    @Getter
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private String tokenType;

        public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn) {
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .tokenType("Bearer")
                    .build();
        }
    }

    /**
     * 토큰 재발급 요청 DTO
     */
    @Getter
    public static class RefreshTokenRequest {

        @NotBlank(message = "Refresh Token은 필수입니다.")
        private String refreshToken;
    }

    /**
     * 로그아웃 요청 DTO
     */
    @Getter
    public static class LogoutRequest {

        @NotBlank(message = "사용자 ID는 필수입니다.")
        private String userId;
    }
}