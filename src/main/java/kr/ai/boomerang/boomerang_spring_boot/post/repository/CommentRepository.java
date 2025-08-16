package kr.ai.boomerang.boomerang_spring_boot.post.repository;

import kr.ai.boomerang.boomerang_spring_boot.post.domain.Comment;
import kr.ai.boomerang.boomerang_spring_boot.post.domain.Post;
import kr.ai.boomerang.boomerang_spring_boot.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 Repository 인터페이스
 *
 * @author Boomerang Team
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글별 댓글 목록 조회 (작성일순)
     *
     * @param post 게시글
     * @return List<Comment>
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * 작성자별 댓글 목록 조회 (페이지네이션)
     *
     * @param author 작성자
     * @param pageable 페이지네이션 정보
     * @return Page<Comment>
     */
    Page<Comment> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 게시글의 댓글 개수 조회
     *
     * @param post 게시글
     * @return 댓글 개수
     */
    long countByPost(Post post);

    /**
     * 사용자별 댓글 수 조회
     *
     * @param author 작성자
     * @return 댓글 수
     */
    long countByAuthor(User author);

    /**
     * 특정 기간 내 작성된 댓글 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 댓글 목록
     */
    @Query("SELECT c FROM Comment c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<Comment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 최근 댓글 조회
     *
     * @param pageable 페이지네이션 정보
     * @return 최근 댓글 목록
     */
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(Pageable pageable);

    /**
     * 게시글별 댓글 수 조회 (통계용)
     *
     * @return 게시글 ID와 댓글 수 매핑
     */
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c GROUP BY c.post.id")
    List<Object[]> countCommentsByPost();
}