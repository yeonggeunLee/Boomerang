package kr.ai.boomerang.boomerang_spring_boot.common.config;

import kr.ai.boomerang.boomerang_spring_boot.auth.security.JwtAuthenticationEntryPoint;
import kr.ai.boomerang.boomerang_spring_boot.auth.security.JwtAuthenticationFilter;
import kr.ai.boomerang.boomerang_spring_boot.auth.security.OAuth2AuthenticationFailureHandler;
import kr.ai.boomerang.boomerang_spring_boot.auth.security.OAuth2AuthenticationSuccessHandler;
import kr.ai.boomerang.boomerang_spring_boot.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 인증, OAuth2 소셜 로그인, CORS 설정을 포함합니다.
 *
 * @author Boomerang Team
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    /**
     * Security Filter Chain 설정
     *
     * @param http HttpSecurity 설정 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 오류
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 공개 엔드포인트
                        .requestMatchers(
                                "/", "/api/v1/auth/**", "/api/v1/travel/**",
                                "/oauth2/**", "/login/oauth2/**", "/error",
                                "/h2-console/**", "/actuator/health"
                        ).permitAll()

                        // 관리자 전용 엔드포인트
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 인증된 사용자만 접근 가능한 엔드포인트
                        .requestMatchers("/api/v1/posts/**", "/api/v1/me").authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}