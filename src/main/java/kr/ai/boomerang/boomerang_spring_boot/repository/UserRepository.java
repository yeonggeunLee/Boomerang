package kr.ai.boomerang.boomerang_spring_boot.repository;

import kr.ai.boomerang.boomerang_spring_boot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository 인터페이스
 *
 * @author Boomerang Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자 조회
     *
     * @param nickname 닉네임
     * @return Optional<User>
     */
    Optional<User> findByNickname(String nickname);

    /**
     * OAuth2 제공자와 제공자 ID로 사용자 조회
     *
     * @param provider 제공자
     * @param providerId 제공자 ID
     * @return Optional<User>
     */
    Optional<User> findByProviderAndProviderId(User.Provider provider, String providerId);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부 확인
     *
     * @param nickname 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 역할별 사용자 수 조회
     *
     * @param role 사용자 역할
     * @return 사용자 수
     */
    long countByRole(User.Role role);

    /**
     * 활성 사용자 목록 조회 (최근 30일 이내 로그인)
     *
     * @return 활성 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.updatedAt >= :thirtyDaysAgo")
    List<User> findActiveUsers(@Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo);

    /**
     * 닉네임 대소문자 구분 없이 조회
     *
     * @param nickname 닉네임
     * @return Optional<User>
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.nickname) = LOWER(:nickname)")
    Optional<User> findByNicknameIgnoreCase(@Param("nickname") String nickname);

    /**
     * 닉네임 부분 검색
     *
     * @param nickname 닉네임 일부
     * @return 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname%")
    java.util.List<User> findByNicknameContaining(@Param("nickname") String nickname);

    /**
     * 이메일과 제공자로 사용자 조회
     *
     * @param email 이메일
     * @param provider OAuth2 제공자
     * @return Optional<User>
     */
    Optional<User> findByEmailAndProvider(String email, User.Provider provider);

    /**
     * 특정 역할을 가진 사용자 목록 조회
     *
     * @param role 사용자 역할
     * @return 사용자 목록
     */
    java.util.List<User> findByRole(User.Role role);
}