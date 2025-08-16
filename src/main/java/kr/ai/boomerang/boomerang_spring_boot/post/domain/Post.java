package kr.ai.boomerang.boomerang_spring_boot.post.domain;

import kr.ai.boomerang.boomerang_spring_boot.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import kr.ai.boomerang.boomerang_spring_boot.user.domain.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 엔티티
 * 사용자가 작성한 여행 정보 게시글을 저장합니다.
 *
 * @author Boomerang Team
 */
@Entity
@Table(name = "posts")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * 게시글 제목 및 내용 업데이트
     *
     * @param title 새로운 제목
     * @param content 새로운 내용
     */
    public void update(String title, String content) {
        this.title = title;
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