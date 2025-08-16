package kr.ai.boomerang.boomerang_spring_boot.auth.service;

import kr.ai.boomerang.boomerang_spring_boot.user.domain.User;
import kr.ai.boomerang.boomerang_spring_boot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 사용자 정보를 처리하는 서비스
 *
 * @author Boomerang Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("OAuth2 사용자 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리에 실패했습니다.");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2 사용자 정보 파싱
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("OAuth2 제공자에서 이메일을 찾을 수 없습니다.");
        }

        // 사용자 조회 또는 생성
        User user = findOrCreateUser(oAuth2UserInfo, registrationId);

        // 사용자 정보를 포함한 OAuth2User 반환
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());
        attributes.put("role", user.getRole().name());

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_" + user.getRole().name()),
                attributes,
                userNameAttributeName
        );
    }

    private User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        User.Provider provider = User.Provider.valueOf(registrationId.toUpperCase());

        return userRepository.findByProviderAndProviderId(provider, oAuth2UserInfo.getId())
                .orElseGet(() -> {
                    String uniqueNickname = generateUniqueNickname(oAuth2UserInfo.getName());

                    User newUser = User.builder()
                            .email(oAuth2UserInfo.getEmail())
                            .nickname(uniqueNickname)
                            .provider(provider)
                            .providerId(oAuth2UserInfo.getId())
                            .role(User.Role.USER)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    log.info("새 OAuth2 사용자 생성: userId={}, provider={}, email={}",
                            savedUser.getId(), provider, oAuth2UserInfo.getEmail());

                    return savedUser;
                });
    }

    private String generateUniqueNickname(String name) {
        String baseNickname = name != null ? name : "사용자";
        String nickname = baseNickname;
        int counter = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + counter;
            counter++;
        }

        return nickname;
    }
/*

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("OAuth2 사용자 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리에 실패했습니다.");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo =
                OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        // 이메일을 선택값으로 처리: 없으면 대체 이메일 생성 or null 허용 정책 선택
        String email = oAuth2UserInfo.getEmail();
        if (email == null || email.isBlank()) {
            // 정책 1) null 허용: 아래에서 newUser 생성 시 email에 null 저장
            // 정책 2) 대체 이메일 생성: 주석 해제해 사용
            // email = oAuth2UserInfo.getId() + "@kakao.local";
        }

        User user = findOrCreateUser(oAuth2UserInfo, registrationId, email);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());
        attributes.put("role", user.getRole().name());

        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_" + user.getRole().name()),
                attributes,
                userNameAttributeName
        );
    }

    private User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo, String registrationId, String resolvedEmail) {
        User.Provider provider = User.Provider.valueOf(registrationId.toUpperCase());

        return userRepository.findByProviderAndProviderId(provider, oAuth2UserInfo.getId())
                .orElseGet(() -> {
                    String uniqueNickname = generateUniqueNickname(oAuth2UserInfo.getName());

                    User newUser = User.builder()
                            .email(resolvedEmail) // null 가능 (엔티티 nullable=true일 때)
                            .nickname(uniqueNickname)
                            .provider(provider)
                            .providerId(oAuth2UserInfo.getId())
                            .role(User.Role.USER)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    log.info("새 OAuth2 사용자 생성: userId={}, provider={}, email={}",
                            savedUser.getId(), provider, resolvedEmail);

                    return savedUser;
                });
    }

    private String generateUniqueNickname(String name) {
        String baseNickname = (name != null && !name.isBlank()) ? name : "사용자";
        String nickname = baseNickname;
        int counter = 1;

        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + counter;
            counter++;
        }
        return nickname;
    }
*/

}