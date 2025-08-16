package kr.ai.boomerang.boomerang_spring_boot.travel.service;

import kr.ai.boomerang.boomerang_spring_boot.travel.dto.TravelDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 여행 정보 서비스
 * 한국관광공사 TourAPI를 연동하여 여행 정보를 제공합니다.
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TravelService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${tour.api.base-url}")
    private String tourApiBaseUrl;

    @Value("${tour.api.service-key}")
    private String serviceKey;

    /**
     * 여행 정보 검색
     *
     * @param query 검색 키워드
     * @param areaCode 지역 코드
     * @param pageNo 페이지 번호
     * @param numOfRows 페이지당 결과 수
     * @return 여행 정보 검색 결과
     */
    public TravelDto.SearchResponse searchTravelInfo(String query, String areaCode, int pageNo, int numOfRows) {
        try {
            String apiUrl = buildApiUrl(query, areaCode, pageNo, numOfRows);
            log.debug("TourAPI 호출: {}", apiUrl);

            String response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseApiResponse(response, pageNo, numOfRows);

        } catch (WebClientException e) {
            log.error("TourAPI 호출 중 네트워크 오류 발생: {}", e.getMessage());
            return createEmptyResponse(pageNo, numOfRows);
        } catch (Exception e) {
            log.error("여행 정보 검색 중 오류 발생: {}", e.getMessage(), e);
            return createEmptyResponse(pageNo, numOfRows);
        }
    }

    /**
     * 지역별 관광지 정보 조회
     *
     * @param areaCode 지역 코드
     * @param sigunguCode 시군구 코드 (선택)
     * @param pageNo 페이지 번호
     * @param numOfRows 페이지당 결과 수
     * @return 지역별 관광지 정보
     */
    public TravelDto.SearchResponse getTravelInfoByArea(String areaCode, String sigunguCode, int pageNo, int numOfRows) {
        try {
            String apiUrl = buildAreaBasedApiUrl(areaCode, sigunguCode, pageNo, numOfRows);
            log.debug("TourAPI 지역별 조회: {}", apiUrl);

            String response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseApiResponse(response, pageNo, numOfRows);

        } catch (Exception e) {
            log.error("지역별 여행 정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return createEmptyResponse(pageNo, numOfRows);
        }
    }

    // === Private Methods ===

    /**
     * 키워드 검색 API URL 생성
     */
    private String buildApiUrl(String query, String areaCode, int pageNo, int numOfRows) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tourApiBaseUrl)
                .path("/KorService1/searchKeyword1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "Boomerang")
                .queryParam("_type", "json")
                .queryParam("listYN", "Y")
                .queryParam("arrange", "A"); // 제목순 정렬

        if (StringUtils.hasText(query)) {
            builder.queryParam("keyword", URLEncoder.encode(query, StandardCharsets.UTF_8));
        }

        if (StringUtils.hasText(areaCode)) {
            builder.queryParam("areaCode", areaCode);
        }

        return builder.build().toUriString();
    }

    /**
     * 지역 기반 API URL 생성
     */
    private String buildAreaBasedApiUrl(String areaCode, String sigunguCode, int pageNo, int numOfRows) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tourApiBaseUrl)
                .path("/KorService1/areaBasedList1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "Boomerang")
                .queryParam("_type", "json")
                .queryParam("listYN", "Y")
                .queryParam("arrange", "A");

        if (StringUtils.hasText(areaCode)) {
            builder.queryParam("areaCode", areaCode);
        }

        if (StringUtils.hasText(sigunguCode)) {
            builder.queryParam("sigungucode", sigunguCode);
        }

        return builder.build().toUriString();
    }

    /**
     * API 응답 파싱
     */
    private TravelDto.SearchResponse parseApiResponse(String response, int pageNo, int numOfRows) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode responseNode = rootNode.path("response");

            // 에러 체크
            JsonNode headerNode = responseNode.path("header");
            String resultCode = headerNode.path("resultCode").asText();
            if (!"0000".equals(resultCode)) {
                String resultMsg = headerNode.path("resultMsg").asText();
                log.warn("TourAPI 응답 오류: {} - {}", resultCode, resultMsg);
                return createEmptyResponse(pageNo, numOfRows);
            }

            JsonNode bodyNode = responseNode.path("body");
            JsonNode itemsNode = bodyNode.path("items");

            List<TravelDto.TravelInfo> travelInfos = new ArrayList<>();

            if (itemsNode.path("item").isArray()) {
                for (JsonNode itemNode : itemsNode.path("item")) {
                    TravelDto.TravelInfo travelInfo = parseTravelInfo(itemNode);
                    travelInfos.add(travelInfo);
                }
            } else if (!itemsNode.path("item").isMissingNode()) {
                // 단일 결과인 경우
                TravelDto.TravelInfo travelInfo = parseTravelInfo(itemsNode.path("item"));
                travelInfos.add(travelInfo);
            }

            int totalCount = bodyNode.path("totalCount").asInt(0);

            return TravelDto.SearchResponse.of(travelInfos, totalCount, pageNo, numOfRows);

        } catch (Exception e) {
            log.error("API 응답 파싱 중 오류 발생: {}", e.getMessage(), e);
            return createEmptyResponse(pageNo, numOfRows);
        }
    }

    /**
     * 개별 여행 정보 파싱
     */
    private TravelDto.TravelInfo parseTravelInfo(JsonNode itemNode) {
        return TravelDto.TravelInfo.of(
                itemNode.path("contentid").asText(),
                itemNode.path("title").asText(),
                itemNode.path("addr1").asText(),
                itemNode.path("addr2").asText(),
                itemNode.path("firstimage").asText(),
                itemNode.path("firstimage2").asText(),
                itemNode.path("areacode").asText(),
                itemNode.path("sigungucode").asText(),
                itemNode.path("cat1").asText(),
                itemNode.path("cat2").asText(),
                itemNode.path("cat3").asText(),
                itemNode.path("mapx").asText(),
                itemNode.path("mapy").asText(),
                itemNode.path("mlevel").asText(),
                itemNode.path("tel").asText()
        );
    }

    /**
     * 빈 응답 생성
     */
    private TravelDto.SearchResponse createEmptyResponse(int pageNo, int numOfRows) {
        return TravelDto.SearchResponse.of(new ArrayList<>(), 0, pageNo, numOfRows);
    }
}