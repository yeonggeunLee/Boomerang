package kr.ai.boomerang.boomerang_spring_boot.auth.exception;

/**
 * 권한이 없을 때 발생하는 예외
 *
 * @author Boomerang Team
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}