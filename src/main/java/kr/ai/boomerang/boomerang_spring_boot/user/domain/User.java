package kr.ai.boomerang.boomerang_spring_boot.user.domain;

import kr.ai.boomerang.boomerang_spring_boot.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * OAuth2 소셜 로그인 정보와 사용자 기본 정보를 저장합니다.
 *
 * @author Boomerang Team
 */
@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * OAuth2 제공자 열거형
     */
    public enum Provider {
        GOOGLE, KAKAO
    }

    /**
     * 사용자 역할 열거형
     */
    public enum Role {
        USER, ADMIN
    }

    /**
     * 닉네임 업데이트
     *
     * @param nickname 새로운 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 역할 업데이트
     *
     * @param role 새로운 역할
     */
    public void updateRole(Role role) {
        this.role = role;
    }
}
