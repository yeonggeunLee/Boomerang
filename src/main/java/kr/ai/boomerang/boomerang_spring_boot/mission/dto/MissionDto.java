package kr.ai.boomerang.boomerang_spring_boot.mission.dto;

import kr.ai.boomerang.boomerang_spring_boot.mission.domain.Mission;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * 미션 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class MissionDto {

    /**
     * 미션 응답 DTO
     */
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String type;
        private Integer targetCount;
        private Integer rewardPoints;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Mission mission) {
            return Response.builder()
                    .id(mission.getId())
                    .title(mission.getTitle())
                    .description(mission.getDescription())
                    .type(mission.getType().name())
                    .targetCount(mission.getTargetCount())
                    .rewardPoints(mission.getRewardPoints())
                    .active(mission.getActive())
                    .createdAt(mission.getCreatedAt())
                    .updatedAt(mission.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 미션 생성 요청 DTO
     */
    @Getter
    public static class CreateRequest {

        @NotBlank(message = "미션 제목은 필수입니다.")
        private String title;

        @NotBlank(message = "미션 설명은 필수입니다.")
        private String description;

        @NotBlank(message = "미션 타입은 필수입니다.")
        private String type;

        @NotNull(message = "목표 수량은 필수입니다.")
        @Positive(message = "목표 수량은 양수여야 합니다.")
        private Integer targetCount;

        @NotNull(message = "보상 포인트는 필수입니다.")
        @Positive(message = "보상 포인트는 양수여야 합니다.")
        private Integer rewardPoints;
    }
}