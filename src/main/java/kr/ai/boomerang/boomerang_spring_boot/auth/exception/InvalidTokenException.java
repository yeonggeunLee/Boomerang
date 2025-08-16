package kr.ai.boomerang.boomerang_spring_boot.auth.exception;

/**
 * 잘못된 토큰일 때 발생하는 예외
 *
 * @author Boomerang Team
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}