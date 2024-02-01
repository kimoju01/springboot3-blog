package me.hyeju.springboot3blog.config;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.config.jwt.TokenProvider;
import me.hyeju.springboot3blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.hyeju.springboot3blog.config.oauth.OAuth2SuccessHandler;
import me.hyeju.springboot3blog.config.oauth.OAuth2UserCustomService;
import me.hyeju.springboot3blog.repository.RefreshTokenRepository;
import me.hyeju.springboot3blog.service.UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() {
        // 스프링 시큐리티 보안 기능(인증, 인가) 비활성화할 경로 설정
        return (web) -> web.ignoring()  // 보안 필터 무시할 경로 설정
                .requestMatchers(PathRequest.toH2Console()) // H2 콘솔과 관련된 요청들은 보안 필터 무시
                .requestMatchers("/img/**", "/css/**", "/js/**");   // 정적 자원과 관련된 요청들은 보안 필터 무시
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 토큰 방식으로 인증을 하기 때문에 기존에 사용하던 폼로그인(세션 기반 인증) 비활성화

        // 폼로그인 설정 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        // 세션 관리 설정. 세션 생성 정책을 STATELESS로 해서 세션 사용 안 함
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 헤더값을 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // HTTP 요청에 대한 권한 설정
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/api/token").permitAll()  // 토큰 재발급 URL은 인증 없이 접근 가능하도록 설정
                .requestMatchers("/api/**").authenticated() // 나머지 API URL은 인증 필요
                .anyRequest().permitAll());

        // OAuth2 로그인 설정
        // OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록 인증 요청과 관련된 상태를 저장할 저장소를 설정
        http.oauth2Login(oauth2Login -> oauth2Login
                .loginPage("/login")
                // Authorization 요청과 관련된 상태 저장
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 (OAuth2 로그인 성공 후) 실행할 핸들러
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                        .userService(oAuth2UserCustomService)));    // OAuth2 사용자 정보 엔드포인트에서 사용할 사용자 서비스 설정

        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutSuccessUrl("/login"));

        // 예외 처리 설정
        // "/api"로 시작하는 URL인 경우 인증 실패 시 401 상태 코드(UNAUTHORIZED)를 반환하도록 예외 처리
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")));

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
