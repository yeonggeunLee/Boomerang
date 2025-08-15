package kr.ai.boomerang.boomerang_spring_boot.service;

import kr.ai.boomerang.boomerang_spring_boot.dto.AuthDto;
import kr.ai.boomerang.boomerang_spring_boot.exception.InvalidTokenException;
import kr.ai.boomerang.boomerang_spring_boot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 인증 서비스
 * JWT 토큰 관리 및 로그아웃 기능을 제공합니다.
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final UserService userService;

    /**
     * Refresh Token을 이용하여 Access Token 재발급
     *
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 정보
     */
    public AuthDto.TokenResponse refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis에 저장된 Refresh Token과 비교
        String storedRefreshToken = tokenService.getRefreshToken(userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new InvalidTokenException("Refresh Token이 일치하지 않습니다.");
        }

        // 사용자 정보 조회
        var user = userService.getUser(userId);

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // 새로운 Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(userId, newRefreshToken);

        log.info("Access Token 재발급 완료: userId={}", userId);

        return AuthDto.TokenResponse.of(
                newAccessToken,
                newRefreshToken,
                jwtTokenProvider.getAccessTokenValidityInMilliseconds()
        );
    }

    /**
     * 로그아웃
     * Redis에서 Refresh Token을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    public void logout(Long userId) {
        tokenService.deleteRefreshToken(userId);
        log.info("로그아웃 완료: userId={}", userId);
    }

    /**
     * 사용자 토큰 생성 (OAuth2 로그인 시 사용)
     *
     * @param userId 사용자 ID
     * @param role 사용자 역할
     * @return 토큰 응답
     */
    public AuthDto.TokenResponse generateTokens(Long userId, String role) {
        String accessToken = jwtTokenProvider.createAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Refresh Token을 Redis에 저장
        tokenService.saveRefreshToken(userId, refreshToken);

        log.info("토큰 생성 완료: userId={}, role={}", userId, role);

        return AuthDto.TokenResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidityInMilliseconds()
        );
    }
}