package kr.ai.boomerang.boomerang_spring_boot.security;

import kr.ai.boomerang.boomerang_spring_boot.dto.AuthDto;
import kr.ai.boomerang.boomerang_spring_boot.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 응답하는 핸들러
 *
 * @author Boomerang Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Long userId = oAuth2User.getAttribute("userId");
        String role = oAuth2User.getAttribute("role");

        if (userId == null || role == null) {
            log.error("OAuth2 인증 성공했으나 사용자 정보가 없습니다.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
            return;
        }

        try {
            // JWT 토큰 생성
            AuthDto.TokenResponse tokenResponse = authService.generateTokens(userId, role);

            // JSON 응답
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));

            log.info("OAuth2 로그인 성공: userId={}", userId);

        } catch (Exception e) {
            log.error("OAuth2 인증 처리 중 오류 발생: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
        }
    }
}