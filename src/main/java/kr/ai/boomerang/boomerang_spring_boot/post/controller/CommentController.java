package kr.ai.boomerang.boomerang_spring_boot.post.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.post.dto.CommentDto;
import kr.ai.boomerang.boomerang_spring_boot.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 관련 API 컨트롤러
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 수정
     *
     * @param commentId 댓글 ID
     * @param request 댓글 수정 요청
     * @param authentication 인증 정보
     * @return 수정된 댓글 정보
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto.Response>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentDto.UpdateRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        CommentDto.Response comment = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 수정되었습니다.", comment));
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다.", null));
    }
}