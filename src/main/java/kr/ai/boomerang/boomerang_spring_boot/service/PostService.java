package kr.ai.boomerang.boomerang_spring_boot.service;

import kr.ai.boomerang.boomerang_spring_boot.domain.Post;
import kr.ai.boomerang.boomerang_spring_boot.domain.User;
import kr.ai.boomerang.boomerang_spring_boot.dto.PostDto;
import kr.ai.boomerang.boomerang_spring_boot.exception.ResourceNotFoundException;
import kr.ai.boomerang.boomerang_spring_boot.exception.UnauthorizedException;
import kr.ai.boomerang.boomerang_spring_boot.repository.PostRepository;
import kr.ai.boomerang.boomerang_spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 관리 서비스
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @return Page<PostDto.ListResponse>
     */
    public Page<PostDto.ListResponse> getPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostDto.ListResponse::from);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId 게시글 ID
     * @return PostDto.Response
     */
    public PostDto.Response getPost(Long postId) {
        Post post = findPostById(postId);
        return PostDto.Response.from(post);
    }

    /**
     * 게시글 생성
     *
     * @param request 게시글 생성 요청
     * @param userId 작성자 ID
     * @return PostDto.Response
     */
    @Transactional
    public PostDto.Response createPost(PostDto.CreateRequest request, Long userId) {
        User author = findUserById(userId);

        Post post = Post.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .author(author)
                .build();

        Post savedPost = postRepository.save(post);
        log.info("게시글 생성 완료: postId={}, authorId={}, title={}",
                savedPost.getId(), userId, request.getTitle());

        return PostDto.Response.from(savedPost);
    }

    /**
     * 게시글 수정
     *
     * @param postId 게시글 ID
     * @param request 게시글 수정 요청
     * @param userId 사용자 ID
     * @return PostDto.Response
     */
    @Transactional
    public PostDto.Response updatePost(Long postId, PostDto.UpdateRequest request, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);

        // 권한 확인
        validatePostUpdatePermission(post, user);

        post.update(request.getTitle().trim(), request.getContent().trim());
        Post savedPost = postRepository.save(post);

        log.info("게시글 수정 완료: postId={}, userId={}, title={}", postId, userId, request.getTitle());
        return PostDto.Response.from(savedPost);
    }

    /**
     * 게시글 삭제
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPostById(postId);
        User user = findUserById(userId);

        // 권한 확인
        validatePostDeletePermission(post, user);

        postRepository.delete(post);
        log.info("게시글 삭제 완료: postId={}, userId={}", postId, userId);
    }

    /**
     * 사용자별 게시글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return Page<PostDto.ListResponse>
     */
    public Page<PostDto.ListResponse> getPostsByUser(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return postRepository.findByAuthorOrderByCreatedAtDesc(user, pageable)
                .map(PostDto.ListResponse::from);
    }

    /**
     * 게시글 검색
     *
     * @param keyword 검색 키워드
     * @param pageable 페이지네이션 정보
     * @return Page<PostDto.ListResponse>
     */
    public Page<PostDto.ListResponse> searchPosts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPosts(pageable);
        }

        String trimmedKeyword = keyword.trim();
        return postRepository.findByTitleContainingOrContentContaining(trimmedKeyword, trimmedKeyword, pageable)
                .map(PostDto.ListResponse::from);
    }

    /**
     * 인기 게시글 조회
     *
     * @param pageable 페이지네이션 정보
     * @return Page<PostDto.ListResponse>
     */
    public Page<PostDto.ListResponse> getPopularPosts(Pageable pageable) {
        return postRepository.findPopularPosts(pageable)
                .map(PostDto.ListResponse::from);
    }

    // === Private Methods ===

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validatePostUpdatePermission(Post post, User user) {
        if (!post.isAuthor(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("게시글 수정 권한이 없습니다.");
        }
    }

    private void validatePostDeletePermission(Post post, User user) {
        if (!post.isAuthor(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("게시글 삭제 권한이 없습니다.");
        }
    }

    /**
     * 게시글 통계 정보 조회
     *
     * @return 게시글 통계
     */
    public PostStatistics getPostStatistics() {
        long totalPosts = postRepository.count();
        // 추가적인 통계 정보는 필요에 따라 구현

        return PostStatistics.builder()
                .totalPosts(totalPosts)
                .build();
    }

    /**
     * 게시글 통계 DTO
     */
    @lombok.Builder
    @lombok.Getter
    public static class PostStatistics {
        private long totalPosts;
    }
}