package kr.ai.boomerang.boomerang_spring_boot.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 실패 시 처리하는 핸들러
 *
 * @author Boomerang Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        String errorCode = "OAUTH2_LOGIN_FAILED";
        if (exception instanceof OAuth2AuthenticationException) {
            errorCode = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
        }


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "소셜 로그인에 실패했습니다." + exception.getMessage());
        errorResponse.put("code", errorCode);
        errorResponse.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}