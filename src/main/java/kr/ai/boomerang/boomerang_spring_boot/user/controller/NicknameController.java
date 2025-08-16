package kr.ai.boomerang.boomerang_spring_boot.user.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 닉네임 관련 API 컨트롤러
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/nickname")
@RequiredArgsConstructor
public class NicknameController {

    private final UserService userService;

    /**
     * 닉네임 중복 확인
     *
     * @param nickname 확인할 닉네임
     * @return 중복 여부
     */
    @GetMapping("/check/{nickname}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkNickname(@PathVariable String nickname) {
        boolean isDuplicated = userService.isNicknameDuplicated(nickname);

        Map<String, Object> result = Map.of(
                "nickname", nickname,
                "isDuplicated", isDuplicated,
                "message", isDuplicated ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다."
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 닉네임 제안
     *
     * @param baseName 기본 닉네임
     * @param count 제안할 개수 (기본값: 1)
     * @return 사용 가능한 닉네임 목록
     */
    @GetMapping("/suggest/{baseName}")
    public ResponseEntity<ApiResponse<List<String>>> suggestNicknames(
            @PathVariable String baseName,
            @RequestParam(defaultValue = "1") int count) {

        List<String> suggestions = userService.suggestAvailableNicknames(baseName, count);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }
}
