package kr.ai.boomerang.boomerang_spring_boot.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 *
 * @author Boomerang Team
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    /**
     * Access Token 생성
     *
     * @param userId 사용자 ID
     * @param role 사용자 역할
     * @return Access Token
     */
    public String createAccessToken(Long userId, String role) {
        return createToken(userId, role, accessTokenValidityInMilliseconds);
    }

    /**
     * Refresh Token 생성
     *
     * @param userId 사용자 ID
     * @return Refresh Token
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, null, refreshTokenValidityInMilliseconds);
    }

    /**
     * JWT 토큰 생성
     *
     * @param userId 사용자 ID
     * @param role 사용자 역할 (Refresh Token의 경우 null)
     * @param validityInMilliseconds 토큰 유효 시간
     * @return JWT 토큰
     */
    private String createToken(Long userId, String role, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        if (role != null) {
            claims.put("role", role);
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * 토큰에서 사용자 역할 추출
     *
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String getUserRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("잘못된 JWT 토큰입니다: {}", e.getMessage());
        } catch (SecurityException | IllegalArgumentException e) {
            log.debug("잘못된 JWT 서명입니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰에서 Claims 정보 추출
     *
     * @param token JWT 토큰
     * @return Claims
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Access Token 만료 시간 반환
     *
     * @return Access Token 만료 시간 (밀리초)
     */
    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInMilliseconds;
    }

    /**
     * Refresh Token 만료 시간 반환
     *
     * @return Refresh Token 만료 시간 (밀리초)
     */
    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }
}