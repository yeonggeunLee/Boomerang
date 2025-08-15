package kr.ai.boomerang.boomerang_spring_boot.domain;

import kr.ai.boomerang.boomerang_spring_boot.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 미션 엔티티
 * 리워드 시스템의 미션 정보를 저장합니다.
 *
 * @author Boomerang Team
 */
@Entity
@Table(name = "missions")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    @Column(nullable = false)
    private Integer targetCount;

    @Column(nullable = false)
    private Integer rewardPoints;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * 미션 타입 열거형
     */
    public enum MissionType {
        POST_CREATION,      // 게시글 작성
        COMMENT_CREATION,   // 댓글 작성
        LOGIN_COUNT        // 로그인 횟수
    }

    /**
     * 미션 활성화/비활성화
     *
     * @param active 활성화 상태
     */
    public void updateActive(Boolean active) {
        this.active = active;
    }
}