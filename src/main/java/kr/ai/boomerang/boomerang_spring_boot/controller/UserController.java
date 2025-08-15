package kr.ai.boomerang.boomerang_spring_boot.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.dto.CommentDto;
import kr.ai.boomerang.boomerang_spring_boot.dto.PostDto;
import kr.ai.boomerang.boomerang_spring_boot.dto.UserDto;
import kr.ai.boomerang.boomerang_spring_boot.service.CommentService;
import kr.ai.boomerang.boomerang_spring_boot.service.PostService;
import kr.ai.boomerang.boomerang_spring_boot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API 컨트롤러
 * 마이페이지 기능을 제공합니다.
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    /**
     * 내 정보 조회
     *
     * @param authentication 인증 정보
     * @return 사용자 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto.Response>> getMyInfo(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDto.Response user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 내 정보 수정
     *
     * @param request 사용자 정보 수정 요청
     * @param authentication 인증 정보
     * @return 수정된 사용자 정보
     */
    @PutMapping
    public ResponseEntity<ApiResponse<UserDto.Response>> updateMyInfo(
            @Valid @RequestBody UserDto.UpdateRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        UserDto.Response user = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다.", user));
    }

    /**
     * 내 게시글 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @param authentication 인증 정보
     * @return 내 게시글 목록
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostDto.ListResponse>>> getMyPosts(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        Page<PostDto.ListResponse> posts = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 내 댓글 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @param authentication 인증 정보
     * @return 내 댓글 목록
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentDto.Response>>> getMyComments(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        Page<CommentDto.Response> comments = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
}