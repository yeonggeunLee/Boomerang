package kr.ai.boomerang.boomerang_spring_boot.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT Token Redis 관리 서비스
 * Refresh Token의 저장, 조회, 삭제를 담당합니다.
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7일 (초 단위)

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Refresh Token 저장
     * -특정 사용자의 리프레시 토큰을 Redis에 저장. 만료시간도 함께 설정
     *
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        log.info("Refresh Token 저장 완료: userId={}", userId);
    }

    /**
     * Refresh Token 조회
     * - Redis에서 특정 사용자 ID와 연관된 리프레시 토큰을 조회
     *
     * @param userId 사용자 ID
     * @return Refresh Token
     */
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 삭제
     * - 특정 사용자의 리프레시 토큰을 삭제
     *
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제 완료: userId={}", userId);
    }

    /**
     * Refresh Token 존재 여부 확인
     * - 특정 사용자의 리프레시 토큰 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    public boolean existsRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 모든 사용자의 Refresh Token 삭제 (관리자)
     * - 관리자용으로 전체 사용자의 리프레시 토큰을 삭제
     * - 미완성 (관리자 검증)
     *
     * @param userId 사용자 ID
     */
    public void deleteAllRefreshTokens(Long userId) {
        String pattern = REFRESH_TOKEN_PREFIX + "*";
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("모든 Refresh Token 삭제 완료: adminUserId={}", userId);
        }
    }
}