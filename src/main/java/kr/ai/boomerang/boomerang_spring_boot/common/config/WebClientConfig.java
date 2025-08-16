package kr.ai.boomerang.boomerang_spring_boot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 설정 클래스
 * 외부 API 호출을 위한 WebClient 설정을 제공합니다.
 *
 * @author Boomerang Team
 */
@Configuration
public class WebClientConfig {

    /**
     * 기본 WebClient Bean 생성
     *
     * @return WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }
}
