package kr.ai.boomerang.boomerang_spring_boot.exception;

/**
 * 외부 API 통신 오류 시 발생하는 예외
 *
 * @author Boomerang Team
 */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}