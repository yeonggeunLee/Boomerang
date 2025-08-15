package kr.ai.boomerang.boomerang_spring_boot.exception;

/**
 * 중복된 리소스일 때 발생하는 예외
 *
 * @author Boomerang Team
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}