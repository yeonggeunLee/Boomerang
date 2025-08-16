package kr.ai.boomerang.boomerang_spring_boot.post.dto;

import kr.ai.boomerang.boomerang_spring_boot.post.domain.Comment;
import kr.ai.boomerang.boomerang_spring_boot.user.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 댓글 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class CommentDto {

    /**
     * 댓글 생성 요청 DTO
     */
    @Getter
    public static class CreateRequest {

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 500, message = "댓글은 1자 이상 500자 이하여야 합니다.")
        private String content;
    }

    /**
     * 댓글 수정 요청 DTO
     */
    @Getter
    public static class UpdateRequest {

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(min = 1, max = 500, message = "댓글은 1자 이상 500자 이하여야 합니다.")
        private String content;
    }

    /**
     * 댓글 응답 DTO
     */
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String content;
        private UserDto.Response author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .author(UserDto.Response.from(comment.getAuthor()))
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        }
    }
}