package kr.ai.boomerang.boomerang_spring_boot.post.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.post.dto.CommentDto;
import kr.ai.boomerang.boomerang_spring_boot.post.dto.PostDto;
import kr.ai.boomerang.boomerang_spring_boot.post.service.CommentService;
import kr.ai.boomerang.boomerang_spring_boot.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시글 관련 API 컨트롤러
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * 게시글 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @param search 검색 키워드 (선택)
     * @return 게시글 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostDto.ListResponse>>> getPosts(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search) {

        Page<PostDto.ListResponse> posts = search != null ?
                postService.searchPosts(search, pageable) :
                postService.getPosts(pageable);

        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 인기 게시글 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @return 인기 게시글 목록
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Page<PostDto.ListResponse>>> getPopularPosts(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<PostDto.ListResponse> posts = postService.getPopularPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId 게시글 ID
     * @return 게시글 상세 정보
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto.Response>> getPost(@PathVariable Long postId) {
        PostDto.Response post = postService.getPost(postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    /**
     * 게시글 생성
     *
     * @param request 게시글 생성 요청
     * @param authentication 인증 정보
     * @return 생성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostDto.Response>> createPost(
            @Valid @RequestBody PostDto.CreateRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        PostDto.Response post = postService.createPost(request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 생성되었습니다.", post));
    }

    /**
     * 게시글 수정
     *
     * @param postId 게시글 ID
     * @param request 게시글 수정 요청
     * @param authentication 인증 정보
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto.Response>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDto.UpdateRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        PostDto.Response post = postService.updatePost(postId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", post));
    }

    /**
     * 게시글 삭제
     *
     * @param postId 게시글 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }

    /**
     * 게시글의 댓글 목록 조회
     *
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto.Response>>> getComments(@PathVariable Long postId) {
        List<CommentDto.Response> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 게시글에 댓글 생성
     *
     * @param postId 게시글 ID
     * @param request 댓글 생성 요청
     * @param authentication 인증 정보
     * @return 생성된 댓글 정보
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentDto.Response>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentDto.CreateRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        CommentDto.Response comment = commentService.createComment(postId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 생성되었습니다.", comment));
    }
}