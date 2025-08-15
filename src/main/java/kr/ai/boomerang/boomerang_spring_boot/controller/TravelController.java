package kr.ai.boomerang.boomerang_spring_boot.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.dto.TravelDto;
import kr.ai.boomerang.boomerang_spring_boot.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 여행 정보 API 컨트롤러
 * 외부 관광 API를 통해 여행 정보를 제공합니다.
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelService travelService;

    /**
     * 여행지 정보 검색
     *
     * @param query 검색 키워드
     * @param areaCode 지역 코드 (선택)
     * @param pageNo 페이지 번호 (기본값: 1)
     * @param numOfRows 페이지당 결과 수 (기본값: 10, 최대: 100)
     * @return 여행지 정보 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<TravelDto.SearchResponse>> searchTravelInfo(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String areaCode,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {

        // 최대 결과 수 제한
        numOfRows = Math.min(numOfRows, 100);
        pageNo = Math.max(pageNo, 1);

        TravelDto.SearchResponse result = travelService.searchTravelInfo(query, areaCode, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 지역별 관광지 조회
     *
     * @param areaCode 지역 코드 (필수)
     * @param sigunguCode 시군구 코드 (선택)
     * @param pageNo 페이지 번호 (기본값: 1)
     * @param numOfRows 페이지당 결과 수 (기본값: 10, 최대: 100)
     * @return 지역별 관광지 정보 목록
     */
    @GetMapping("/area/{areaCode}")
    public ResponseEntity<ApiResponse<TravelDto.SearchResponse>> getTravelInfoByArea(
            @PathVariable String areaCode,
            @RequestParam(required = false) String sigunguCode,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows) {

        // 최대 결과 수 제한
        numOfRows = Math.min(numOfRows, 100);
        pageNo = Math.max(pageNo, 1);

        TravelDto.SearchResponse result = travelService.getTravelInfoByArea(areaCode, sigunguCode, pageNo, numOfRows);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 지역 코드 정보 조회 (하드코딩된 기본 정보 제공)
     *
     * @return 지역 코드 정보
     */
    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<java.util.List<AreaInfo>>> getAreaCodes() {
        java.util.List<AreaInfo> areas = java.util.Arrays.asList(
                new AreaInfo("1", "서울"),
                new AreaInfo("2", "인천"),
                new AreaInfo("3", "대전"),
                new AreaInfo("4", "대구"),
                new AreaInfo("5", "광주"),
                new AreaInfo("6", "부산"),
                new AreaInfo("7", "울산"),
                new AreaInfo("8", "세종특별자치시"),
                new AreaInfo("31", "경기도"),
                new AreaInfo("32", "강원도"),
                new AreaInfo("33", "충청북도"),
                new AreaInfo("34", "충청남도"),
                new AreaInfo("35", "경상북도"),
                new AreaInfo("36", "경상남도"),
                new AreaInfo("37", "전라북도"),
                new AreaInfo("38", "전라남도"),
                new AreaInfo("39", "제주도")
        );

        return ResponseEntity.ok(ApiResponse.success(areas));
    }

    /**
     * 지역 정보 DTO
     */
    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class AreaInfo {
        private String code;
        private String name;
    }
}