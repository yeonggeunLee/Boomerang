package kr.ai.boomerang.boomerang_spring_boot.common.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 *
 * @author Boomerang Team
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}







