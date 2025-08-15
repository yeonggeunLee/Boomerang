package kr.ai.boomerang.boomerang_spring_boot.util;

/**
 * 응답 형태 변환 유틸리티 클래스
 *
 * @author Boomerang Team
 */
public class ResponseUtils {

    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return API 응답
     */
    public static <T> kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse<T> success(T data) {
        return kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse.success(data);
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     *
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return API 응답
     */
    public static <T> kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse<T> success(String message, T data) {
        return kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse.success(message, data);
    }

    /**
     * 실패 응답 생성
     *
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return API 응답
     */
    public static <T> kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse<T> error(String message) {
        return kr.ai.boomerang.boomerang_spring_boot.common.ApiResponse.error(message);
    }
}