package me.hyeju.springboot3blog.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.hyeju.springboot3blog.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    // OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록 인증 요청과 관련된 상태를 저장할 저장소
    // AuthorizationRequestRepository? 권한 인증 흐름에서 클라이언트의 요청을 유지하는 데 사용하는 클래스

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";   // OAuth2 권한 요청 관련 정보 저장하기 위한 쿠키

    private static final int COOKIE_EXPIRE_SECONDS = 18000; // 쿠키 만료 시간 18000초

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // HTTP 요청에서 OAuth2 권한 요청 관련 정보 읽어옴
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);  // 쿠키 읽어와서
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);    // 쿠키에 저장된 직렬화된 데이터를 역직렬화해서 OAuth2AuthorizationRequest 객체로 변환
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) { // OAuth2AuthorizationRequest 객체가 null인 경우
            removeAuthorizationRequestCookies(request, response);   // 메서드 호출해서 관련 쿠키 제거
            return;
        }

        // OAuth2AuthorizationRequest 객체가 null이 아닌 경우 객체 직렬화 해서 쿠키에 저장하고 쿠키 만료 시간 설정
        CookieUtil.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECONDS
        );
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        // HTTP 요청에서 OAuth2 권한 요청 관련 정보 제거
        return this.loadAuthorizationRequest(request);  // 메서드 호출해서 쿠키에서 정보 읽어오고 쿠키를 제거한 후 해당 정보 반환
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        // HTTP 요청에서 OAuth2 권한 요청 관련 정보를 저장한 쿠키를 제거
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

}
