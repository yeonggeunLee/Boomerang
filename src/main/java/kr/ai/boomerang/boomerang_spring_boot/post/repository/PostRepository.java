package kr.ai.boomerang.boomerang_spring_boot.post.repository;

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
 * 게시글 Repository 인터페이스
 *
 * @author Boomerang Team
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 작성자별 게시글 목록 조회 (페이지네이션)
     *
     * @param author 작성자
     * @param pageable 페이지네이션 정보
     * @return Page<Post>
     */
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * 게시글 목록 조회 (최신순, 페이지네이션)
     *
     * @param pageable 페이지네이션 정보
     * @return Page<Post>
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 제목이나 내용으로 게시글 검색
     *
     * @param title 제목 검색어
     * @param content 내용 검색어
     * @param pageable 페이지네이션 정보
     * @return Page<Post>
     */
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% OR p.content LIKE %:content% ORDER BY p.createdAt DESC")
    Page<Post> findByTitleContainingOrContentContaining(@Param("title") String title, @Param("content") String content, Pageable pageable);

    /**
     * 특정 기간 내 작성된 게시글 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 게시글 목록
     */
    @Query("SELECT p FROM Post p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Post> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 사용자별 게시글 수 조회
     *
     * @param author 작성자
     * @return 게시글 수
     */
    long countByAuthor(User author);

    /**
     * 인기 게시글 조회 (댓글이 많은 순)
     *
     * @param pageable 페이지네이션 정보
     * @return 인기 게시글 목록
     */
    @Query("SELECT p FROM Post p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c) DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /**
     * 최근 게시글 조회
     *
     * @param limit 조회할 게시글 수
     * @return 최근 게시글 목록
     */
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);
}