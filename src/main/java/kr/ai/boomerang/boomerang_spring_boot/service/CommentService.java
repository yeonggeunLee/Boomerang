package kr.ai.boomerang.boomerang_spring_boot.service;

import kr.ai.boomerang.boomerang_spring_boot.domain.Comment;
import kr.ai.boomerang.boomerang_spring_boot.domain.Post;
import kr.ai.boomerang.boomerang_spring_boot.domain.User;
import kr.ai.boomerang.boomerang_spring_boot.dto.CommentDto;
import kr.ai.boomerang.boomerang_spring_boot.exception.ResourceNotFoundException;
import kr.ai.boomerang.boomerang_spring_boot.exception.UnauthorizedException;
import kr.ai.boomerang.boomerang_spring_boot.repository.CommentRepository;
import kr.ai.boomerang.boomerang_spring_boot.repository.PostRepository;
import kr.ai.boomerang.boomerang_spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 관리 서비스
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글의 댓글 목록 조회
     *
     * @param postId 게시글 ID
     * @return List<CommentDto.Response>
     */
    public List<CommentDto.Response> getCommentsByPost(Long postId) {
        Post post = findPostById(postId);
        return commentRepository.findByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(CommentDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 생성
     *
     * @param postId 게시글 ID
     * @param request 댓글 생성 요청
     * @param userId 작성자 ID
     * @return CommentDto.Response
     */
    @Transactional
    public CommentDto.Response createComment(Long postId, CommentDto.CreateRequest request, Long userId) {
        Post post = findPostById(postId);
        User author = findUserById(userId);

        Comment comment = Comment.builder()
                .content(request.getContent().trim())
                .post(post)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("댓글 생성 완료: commentId={}, postId={}, authorId={}",
                savedComment.getId(), postId, userId);

        return CommentDto.Response.from(savedComment);
    }

    /**
     * 댓글 수정
     *
     * @param commentId 댓글 ID
     * @param request 댓글 수정 요청
     * @param userId 사용자 ID
     * @return CommentDto.Response
     */
    @Transactional
    public CommentDto.Response updateComment(Long commentId, CommentDto.UpdateRequest request, Long userId) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(userId);

        // 권한 확인
        validateCommentUpdatePermission(comment, user);

        comment.update(request.getContent().trim());
        Comment savedComment = commentRepository.save(comment);

        log.info("댓글 수정 완료: commentId={}, userId={}", commentId, userId);
        return CommentDto.Response.from(savedComment);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 댓글 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        User user = findUserById(userId);

        // 권한 확인
        validateCommentDeletePermission(comment, user);

        commentRepository.delete(comment);
        log.info("댓글 삭제 완료: commentId={}, userId={}", commentId, userId);
    }

    /**
     * 사용자별 댓글 목록 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이지네이션 정보
     * @return Page<CommentDto.Response>
     */
    public Page<CommentDto.Response> getCommentsByUser(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        return commentRepository.findByAuthorOrderByCreatedAtDesc(user, pageable)
                .map(CommentDto.Response::from);
    }

    // === Private Methods ===

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateCommentUpdatePermission(Comment comment, User user) {
        if (!comment.isAuthor(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("댓글 수정 권한이 없습니다.");
        }
    }

    private void validateCommentDeletePermission(Comment comment, User user) {
        if (!comment.isAuthor(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("댓글 삭제 권한이 없습니다.");
        }
    }
}