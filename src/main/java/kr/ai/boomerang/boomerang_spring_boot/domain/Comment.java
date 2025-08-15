package kr.ai.boomerang.boomerang_spring_boot.domain;

import kr.ai.boomerang.boomerang_spring_boot.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 댓글 엔티티
 * 게시글에 달린 댓글 정보를 저장합니다.
 *
 * @author Boomerang Team
 */
@Entity
@Table(name = "comments")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 댓글 내용 업데이트
     *
     * @param content 새로운 내용
     */
    public void update(String content) {
        this.content = content;
    }

    /**
     * 작성자 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 작성자 여부
     */
    public boolean isAuthor(Long userId) {
        return this.author.getId().equals(userId);
    }
}