package kr.ai.boomerang.boomerang_spring_boot.user.dto;

import kr.ai.boomerang.boomerang_spring_boot.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 사용자 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class UserDto {

    /**
     * 사용자 응답 DTO
     */
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        private String provider;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(User user) {
            return Response.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .provider(user.getProvider().name())
                    .role(user.getRole().name())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 사용자 프로필 업데이트 요청 DTO
     */
    @Getter
    public static class UpdateRequest {

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
        private String nickname;
    }

    /**
     * 관리자용 사용자 역할 변경 요청 DTO
     */
    @Getter
    public static class RoleUpdateRequest {

        @NotBlank(message = "역할은 필수입니다.")
        private String role;
    }
}