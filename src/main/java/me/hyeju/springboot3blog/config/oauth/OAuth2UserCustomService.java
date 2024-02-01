package me.hyeju.springboot3blog.config.oauth;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.User;
import me.hyeju.springboot3blog.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 바탕으로 유저 정보를 담은 객체 반환

        OAuth2User user = super.loadUser(userRequest);  // 부모 클래스의 loadUser() 메서드 호출해 OAuth2 사용자 정보 가져옴
        saveOrUpdate(user); // 사용자 정보 업데이트하거나 생성

        return user;    // 사용자 정보 반환
    }

    private User saveOrUpdate(OAuth2User oAuth2User) {
        // 사용자가 user 테이블에 있으면 업데이트, 없으면 사용자 새로 생성해서 DB에 저장

        Map<String, Object> attributes = oAuth2User.getAttributes();    // OAuth 사용자 정보에서 속성을 가져와 attributes Map에 저장
        String email = (String) attributes.get("email");    // 이메일 속성을 추출해 문자열로 저장
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))     // 조회된 사용자가 존재하면 해당 엔티티의 닉네임을 업데이트
                .orElse(User.builder()                  // 조회된 사용자가 없으면 새로운 사용자 엔티티 생성
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);
    }

}
