package me.hyeju.springboot3blog.config.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.config.jwt.TokenProvider;
import me.hyeju.springboot3blog.domain.RefreshToken;
import me.hyeju.springboot3blog.domain.User;
import me.hyeju.springboot3blog.repository.RefreshTokenRepository;
import me.hyeju.springboot3blog.service.UserService;
import me.hyeju.springboot3blog.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // OAuth2 인증 성공 시 실행할 핸들러

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 인증 성공 시 실행되는 메서드
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();     // OAuth2User 객체로 형변환해서 사용자 정보 얻음
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));  // 사용자 정보에서 이메일 얻어서 사용자 조회

        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);    // 리프레시 토큰 생성
        saveRefreshToken(user.getId(), refreshToken);       // 리프레시 토큰 저장
        addRefreshTokenToCookie(request, response, refreshToken);   // 리프레시 토큰을 쿠키에 저장

        // 액세스 토큰 생성 -> 패스에 액세스 토큰 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);  // 액세스 토큰 생성
        String targetUrl = getTargetUrl(accessToken);   // /articles?token=ABCDF... 이런 식으로 쿼리 파라미터에 액세스 토큰 추가

        // 인증 관련 설정 값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        // targetUrl로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
        // 생성된 리프레시 토큰을 전달받아 DB에 저장

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))  // 조회한 리프레시 토큰이 존재하면 해당 토큰을 새로 생성한 리프레시 토큰으로 업데이트
                .orElse(new RefreshToken(userId, newRefreshToken)); // 조회한 리프레시 토큰이 없으면 새로 생성한 리프레시 토큰으로 RefreshToken 객체 생성

        refreshTokenRepository.save(refreshToken);  // DB에 저장
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        // 생성된 리프레시 토큰을 쿠키에 저장

        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();    // 리프레시 토큰 유효 기간을 초로 설정
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);  // 기존에 존재하던 리프레시 토큰 삭제
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);  // 변경된 리프레시 토큰 값으로 새로운 쿠키를 생성해 응답에 추가
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        // 인증 관련 설정 값, 쿠키 제거
        // 인증 프로세스 진행하면서 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터 제거

        super.clearAuthenticationAttributes(request);   // 부모 클래스의 메서드 호출해서 인증과 관련된 속성들 가져옴
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);    // OAuth 인증을 위해 저장된 정보도 제거
    }

    private String getTargetUrl(String token) {
        // 액세스 토큰을 패스에 추가

        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)    // REDIRECT_PATH를 기반으로 새로운 URI 만듦
                .queryParam("token", token) // 쿼리 파라미터로 "token" 추가하고 그 값으로 전달받은 token 사용
                .build()
                .toUriString(); // URI를 문자열로 변환해 반환. 이 문자열이 리다이렉트할 대상 URL이 됨
    }

}
