package kr.ai.boomerang.boomerang_spring_boot.mission.repository;

import kr.ai.boomerang.boomerang_spring_boot.mission.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 미션 Repository 인터페이스
 *
 * @author Boomerang Team
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 활성화된 미션 목록 조회
     *
     * @return List<Mission>
     */
    List<Mission> findByActiveTrue();

    /**
     * 비활성화된 미션 목록 조회
     *
     * @return List<Mission>
     */
    List<Mission> findByActiveFalse();

    /**
     * 미션 타입별 활성화된 미션 조회
     *
     * @param type 미션 타입
     * @return List<Mission>
     */
    List<Mission> findByTypeAndActiveTrue(Mission.MissionType type);

    /**
     * 미션 타입별 모든 미션 조회
     *
     * @param type 미션 타입
     * @return List<Mission>
     */
    List<Mission> findByType(Mission.MissionType type);

    /**
     * 제목으로 미션 검색
     *
     * @param title 미션 제목
     * @return Optional<Mission>
     */
    Optional<Mission> findByTitle(String title);

    /**
     * 보상 포인트 범위로 미션 조회
     *
     * @param minPoints 최소 포인트
     * @param maxPoints 최대 포인트
     * @return List<Mission>
     */
    @Query("SELECT m FROM Mission m WHERE m.rewardPoints BETWEEN :minPoints AND :maxPoints AND m.active = true ORDER BY m.rewardPoints DESC")
    List<Mission> findByRewardPointsBetween(@Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints);

    /**
     * 달성 가능한 미션 조회 (목표 수량이 낮은 순)
     *
     * @return List<Mission>
     */
    @Query("SELECT m FROM Mission m WHERE m.active = true ORDER BY m.targetCount ASC")
    List<Mission> findEasyMissions();

    /**
     * 고포인트 미션 조회
     *
     * @return List<Mission>
     */
    @Query("SELECT m FROM Mission m WHERE m.active = true ORDER BY m.rewardPoints DESC")
    List<Mission> findHighRewardMissions();
}