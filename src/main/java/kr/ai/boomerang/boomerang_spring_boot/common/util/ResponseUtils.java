package kr.ai.boomerang.boomerang_spring_boot.common.util;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;

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
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     *
     * @param message 응답 메시지
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return API 응답
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    /**
     * 실패 응답 생성
     *
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return API 응답
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.error(message);
    }
}