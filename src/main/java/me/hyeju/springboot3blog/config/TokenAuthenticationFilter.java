package me.hyeju.springboot3blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    // 액세스 토큰 값이 담긴 Authorization 헤더 값을 가져온 뒤 액세스 토큰이 유효하다면 인증 정보를 설정하는 필터
    // OncePerRequestFilter? 각 요청당 한 번만 실행되도록 보장하는 필터

    private final TokenProvider tokenProvider;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);   // HTTP 요청에서 Authorization 헤더 추출
        String token = getAccessToken(authorizationHeader); // 추출한 헤더에서 토큰을 얻기 위해 getAccessToken() 호출

        if (tokenProvider.validToken(token)) {  // 토큰 유효성 검증
            Authentication authentication = tokenProvider.getAuthentication(token); // 유효한 토큰을 사용해 사용자 인증 정보를 가져오는 getAuthentication() 호출 => 토큰으로 인증 객체 만듦
            SecurityContextHolder.getContext().setAuthentication(authentication);   // 현재 스레드의 SecurityContext에 사용자 인증 정보 설정(다른 스레드와 공유X. 독립적)
        }

        filterChain.doFilter(request, response);    // 다음 필터로 요청을 전달
    }

    private String getAccessToken(String authorizationHeader) {
        // 토큰 접두사(Bearer) 제거하고 토큰 문자열만 얻기 위한 메서드
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

}
