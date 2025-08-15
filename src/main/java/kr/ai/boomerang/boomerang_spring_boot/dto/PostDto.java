package kr.ai.boomerang.boomerang_spring_boot.dto;

import kr.ai.boomerang.boomerang_spring_boot.domain.Post;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class PostDto {

    /**
     * 게시글 생성 요청 DTO
     */
    @Getter
    public static class CreateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다.")
        private String content;
    }

    /**
     * 게시글 수정 요청 DTO
     */
    @Getter
    public static class UpdateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다.")
        private String content;
    }

    /**
     * 게시글 응답 DTO
     */
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private UserDto.Response author;
        private List<CommentDto.Response> comments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Post post) {
            return Response.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(UserDto.Response.from(post.getAuthor()))
                    .comments(post.getComments().stream()
                            .map(CommentDto.Response::from)
                            .collect(Collectors.toList()))
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 게시글 목록 응답 DTO (댓글 제외)
     */
    @Getter
    @Builder
    public static class ListResponse {
        private Long id;
        private String title;
        private String content;
        private UserDto.Response author;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ListResponse from(Post post) {
            return ListResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent().length() > 100 ?
                            post.getContent().substring(0, 100) + "..." : post.getContent())
                    .author(UserDto.Response.from(post.getAuthor()))
                    .commentCount(post.getComments().size())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
}