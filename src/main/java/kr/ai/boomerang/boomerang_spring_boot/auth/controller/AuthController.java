package kr.ai.boomerang.boomerang_spring_boot.auth.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.auth.dto.AuthDto;
import kr.ai.boomerang.boomerang_spring_boot.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 * 토큰 재발급, 로그아웃 등의 인증 관련 기능을 제공합니다.
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 액세스 토큰 재발급
     * 리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급
     *
     * @param request Refresh Token 요청
     * @return 새로운 토큰 정보
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.TokenResponse>> refreshToken(
            @Valid @RequestBody AuthDto.RefreshTokenRequest request) {

        AuthDto.TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", tokenResponse));
    }

    /**
     * 로그아웃
     * Redis에 저장된 리프레시 토큰을 삭제
     *
     * @param authentication 인증 정보
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다.", null));
    }

    /**
     * 토큰 유효성 검증
     * 현재 토큰이 유효한지 확인합니다.
     *
     * @param authentication 인증 정보
     * @return 토큰 유효성 결과
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(Authentication authentication) {
        boolean isValid = authentication != null && authentication.isAuthenticated();
        return ResponseEntity.ok(ApiResponse.success("토큰 유효성 검증 완료", isValid));
    }
}