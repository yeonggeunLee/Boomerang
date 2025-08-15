package kr.ai.boomerang.boomerang_spring_boot.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 유효성 검증 유틸리티 클래스
 *
 * @author Boomerang Team
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern NICKNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9가-힣]{2,20}$"
    );

    /**
     * 이메일 형식 검증
     *
     * @param email 검증할 이메일
     * @return 유효 여부
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 닉네임 형식 검증
     *
     * @param nickname 검증할 닉네임
     * @return 유효 여부
     */
    public static boolean isValidNickname(String nickname) {
        return StringUtils.hasText(nickname) && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    /**
     * SQL Injection 방지를 위한 특수문자 검증
     *
     * @param input 검증할 입력값
     * @return 안전한 여부
     */
    public static boolean isSafeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return true;
        }

        String dangerous = input.toLowerCase();
        String[] sqlKeywords = {"select", "insert", "update", "delete", "drop", "union", "script"};

        for (String keyword : sqlKeywords) {
            if (dangerous.contains(keyword)) {
                return false;
            }
        }

        return true;
    }
}