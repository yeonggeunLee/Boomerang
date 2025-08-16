package kr.ai.boomerang.boomerang_spring_boot.user.service;

import kr.ai.boomerang.boomerang_spring_boot.user.domain.User;
import kr.ai.boomerang.boomerang_spring_boot.user.dto.UserDto;
import kr.ai.boomerang.boomerang_spring_boot.common.exception.ResourceNotFoundException;
import kr.ai.boomerang.boomerang_spring_boot.auth.exception.UnauthorizedException;
import kr.ai.boomerang.boomerang_spring_boot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 관리 서비스
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return UserDto.Response
     */
    public UserDto.Response getUser(Long userId) {
        User user = findUserById(userId);
        return UserDto.Response.from(user);
    }

    /**
     * 사용자 프로필 업데이트
     *
     * @param userId 사용자 ID
     * @param request 업데이트 요청
     * @return UserDto.Response
     */
    @Transactional
    public UserDto.Response updateUser(Long userId, UserDto.UpdateRequest request) {
        User user = findUserById(userId);

        // 닉네임 중복 체크 (자신 제외)
        validateNicknameUniqueness(request.getNickname(), userId);

        user.updateNickname(request.getNickname());
        userRepository.save(user);

        log.info("사용자 프로필 업데이트 완료: userId={}, nickname={}", userId, request.getNickname());
        return UserDto.Response.from(user);
    }

    /**
     * 모든 사용자 목록 조회 (관리자용)
     *
     * @param pageable 페이지네이션 정보
     * @return Page<UserDto.Response>
     */
    public Page<UserDto.Response> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto.Response::from);
    }

    /**
     * 사용자 역할 변경 (관리자용)
     *
     * @param userId 대상 사용자 ID
     * @param request 역할 변경 요청
     * @param adminUserId 관리자 사용자 ID
     * @return UserDto.Response
     */
    @Transactional
    public UserDto.Response updateUserRole(Long userId, UserDto.RoleUpdateRequest request, Long adminUserId) {
        // 관리자 권한 확인
        validateAdminPermission(adminUserId);

        // 대상 사용자 조회
        User targetUser = findUserById(userId);

        // 역할 유효성 검증
        User.Role newRole = validateAndParseRole(request.getRole());

        // 역할 업데이트
        targetUser.updateRole(newRole);
        userRepository.save(targetUser);

        log.info("사용자 역할 변경 완료: userId={}, role={}, adminUserId={}", userId, newRole, adminUserId);
        return UserDto.Response.from(targetUser);
    }

    /**
     * 사용자 삭제 (관리자용)
     *
     * @param userId 대상 사용자 ID
     * @param adminUserId 관리자 사용자 ID
     */
    @Transactional
    public void deleteUser(Long userId, Long adminUserId) {
        // 관리자 권한 확인
        validateAdminPermission(adminUserId);

        // 자기 자신은 삭제할 수 없음
        if (userId.equals(adminUserId)) {
            throw new IllegalArgumentException("자기 자신은 삭제할 수 없습니다.");
        }

        // 대상 사용자 조회 및 삭제
        User targetUser = findUserById(userId);
        userRepository.delete(targetUser);

        log.info("사용자 삭제 완료: userId={}, adminUserId={}", userId, adminUserId);
    }

    /**
     * OAuth2 사용자 조회 또는 생성
     *
     * @param provider OAuth2 제공자
     * @param providerId 제공자 사용자 ID
     * @param email 이메일
     * @param nickname 닉네임
     * @return User
     */
    @Transactional
    public User findOrCreateOAuth2User(User.Provider provider, String providerId, String email, String nickname) {
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    String uniqueNickname = generateUniqueNickname(nickname != null ? nickname : "사용자");

                    User newUser = User.builder()
                            .email(email)
                            .nickname(uniqueNickname)
                            .provider(provider)
                            .providerId(providerId)
                            .role(User.Role.USER)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    log.info("새 OAuth2 사용자 생성: userId={}, provider={}, email={}",
                            savedUser.getId(), provider, email);

                    return savedUser;
                });
    }

    // === Private Methods ===

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateAdminPermission(Long adminUserId) {
        User adminUser = findUserById(adminUserId);
        if (adminUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }
    }

    private void validateNicknameUniqueness(String nickname, Long currentUserId) {
        Optional<User> existingUser = userRepository.findByNickname(nickname);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

    private User.Role validateAndParseRole(String roleStr) {
        try {
            return User.Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 역할입니다: " + roleStr);
        }
    }

    private String generateUniqueNickname(String baseName) {
        String nickname = baseName;
        int counter = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseName + counter;
            counter++;
        }

        return nickname;
    }

    /**
     * 닉네임으로 사용자 조회
     *
     * @param nickname 닉네임
     * @return Optional<User>
     */
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    /**
     * 닉네임 중복 체크
     *
     * @param nickname 확인할 닉네임
     * @return 중복 여부 (true: 중복됨, false: 사용가능)
     */
    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 사용 가능한 닉네임 제안 (추가됨)
     *
     * @param baseName 기본 닉네임
     * @param count 제안할 개수
     * @return 사용 가능한 닉네임 목록
     */
    public java.util.List<String> suggestAvailableNicknames(String baseName, int count) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();

        for (int i = 1; suggestions.size() < count && i <= count * 2; i++) {
            String candidateNickname = baseName + i;
            if (!userRepository.existsByNickname(candidateNickname)) {
                suggestions.add(candidateNickname);
            }
        }

        return suggestions;
    }

    /**
     * 사용자 통계 정보 조회
     *
     * @return 사용자 통계
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByRole(User.Role.ADMIN);
        long regularUsers = userRepository.countByRole(User.Role.USER);

        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .adminUsers(adminUsers)
                .regularUsers(regularUsers)
                .build();
    }

    /**
     * 사용자 통계 DTO
     */
    @lombok.Builder
    @lombok.Getter
    public static class UserStatistics {
        private long totalUsers;
        private long adminUsers;
        private long regularUsers;
    }
}