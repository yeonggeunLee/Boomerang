package kr.ai.boomerang.boomerang_spring_boot.admin.controller;

import kr.ai.boomerang.boomerang_spring_boot.common.web.ApiResponse;
import kr.ai.boomerang.boomerang_spring_boot.user.dto.UserDto;
import kr.ai.boomerang.boomerang_spring_boot.post.service.PostService;
import kr.ai.boomerang.boomerang_spring_boot.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용 API 컨트롤러
 *
 * @author Boomerang Team
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    /**
     * 전체 사용자 목록 조회
     *
     * @param pageable 페이지네이션 정보
     * @return 사용자 목록
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDto.Response>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {

        Page<UserDto.Response> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 특정 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto.Response>> getUser(@PathVariable Long userId) {
        UserDto.Response user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 사용자 역할 변경
     *
     * @param userId 대상 사용자 ID
     * @param request 역할 변경 요청
     * @param authentication 인증 정보
     * @return 변경된 사용자 정보
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserDto.Response>> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UserDto.RoleUpdateRequest request,
            Authentication authentication) {

        Long adminUserId = (Long) authentication.getPrincipal();
        UserDto.Response user = userService.updateUserRole(userId, request, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("사용자 역할이 변경되었습니다.", user));
    }

    /**
     * 사용자 강제 탈퇴
     *
     * @param userId 대상 사용자 ID
     * @param authentication 인증 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {

        Long adminUserId = (Long) authentication.getPrincipal();
        userService.deleteUser(userId, adminUserId);
        return ResponseEntity.ok(ApiResponse.success("사용자가 삭제되었습니다.", null));
    }

    /**
     * 사용자 통계 조회
     *
     * @return 사용자 통계 정보
     */
    @GetMapping("/statistics/users")
    public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
        UserService.UserStatistics statistics = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * 게시글 통계 조회
     *
     * @return 게시글 통계 정보
     */
    @GetMapping("/statistics/posts")
    public ResponseEntity<ApiResponse<PostService.PostStatistics>> getPostStatistics() {
        PostService.PostStatistics statistics = postService.getPostStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}