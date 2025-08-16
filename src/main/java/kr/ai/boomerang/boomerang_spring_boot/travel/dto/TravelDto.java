package kr.ai.boomerang.boomerang_spring_boot.travel.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 여행 정보 관련 DTO 클래스들
 *
 * @author Boomerang Team
 */
public class TravelDto {

    /**
     * 여행 정보 검색 응답 DTO
     */
    @Getter
    @Builder
    public static class SearchResponse {
        private List<TravelInfo> items;
        private int totalCount;
        private int pageNo;
        private int numOfRows;
        private boolean hasNext;

        public static SearchResponse of(List<TravelInfo> items, int totalCount, int pageNo, int numOfRows) {
            return SearchResponse.builder()
                    .items(items)
                    .totalCount(totalCount)
                    .pageNo(pageNo)
                    .numOfRows(numOfRows)
                    .hasNext(totalCount > pageNo * numOfRows)
                    .build();
        }
    }

    /**
     * 개별 여행 정보 DTO
     */
    @Getter
    @Builder
    public static class TravelInfo {
        private String contentId;
        private String title;
        private String addr1;
        private String addr2;
        private String fullAddress;
        private String firstImage;
        private String firstImage2;
        private String areaCode;
        private String areaName;
        private String sigunguCode;
        private String sigunguName;
        private String cat1;
        private String cat2;
        private String cat3;
        private String mapX;
        private String mapY;
        private String mlevel;
        private String tel;
        private String homepage;
        private String overview;

        public static TravelInfo of(String contentId, String title, String addr1, String addr2,
                                    String firstImage, String firstImage2, String areaCode, String sigunguCode,
                                    String cat1, String cat2, String cat3, String mapX, String mapY,
                                    String mlevel, String tel) {
            return TravelInfo.builder()
                    .contentId(contentId)
                    .title(title)
                    .addr1(addr1)
                    .addr2(addr2)
                    .fullAddress(buildFullAddress(addr1, addr2))
                    .firstImage(firstImage)
                    .firstImage2(firstImage2)
                    .areaCode(areaCode)
                    .sigunguCode(sigunguCode)
                    .cat1(cat1)
                    .cat2(cat2)
                    .cat3(cat3)
                    .mapX(mapX)
                    .mapY(mapY)
                    .mlevel(mlevel)
                    .tel(tel)
                    .build();
        }

        private static String buildFullAddress(String addr1, String addr2) {
            if (addr1 == null) return "";
            if (addr2 == null || addr2.trim().isEmpty()) return addr1;
            return addr1 + " " + addr2;
        }
    }
}