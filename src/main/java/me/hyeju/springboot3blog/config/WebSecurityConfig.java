//package me.hyeju.springboot3blog.config;
//
//import lombok.RequiredArgsConstructor;
//import me.hyeju.springboot3blog.service.UserDetailService;
//import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//    // 폼로그인 방식 사용하지 않고 OAuth2 + JWT 함께 사용하는 토큰 방식을 사용하기 위해 WebSecurityConfig 대신 WebOAuthSecurityConfig 사용
//
//    private final UserDetailService userService;
//
//    @Bean   // 스프링 컨테이너에 WebSecurityCustomizer 타입 Bean으로 등록. 왜? 스프링 시큐리티 기본 보안 설정을 커스터마이징하기 위해
//    public WebSecurityCustomizer configure() {
//        // 스프링 시큐리티 보안 기능(인증, 인가) 비활성화할 경로 설정
//        return (web) -> web.ignoring()  // 보안 필터 무시할 경로 설정
//                .requestMatchers(PathRequest.toH2Console()) // H2 콘솔과 관련된 요청들은 보안 필터 무시 (ex. http://localhost:8080/h2-console 하위 url들)
//                .requestMatchers("/static/**"); // /static/ 경로로 시작하는 정적 리소스들에 대한 요청들은 보안 필터 무시
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        // 특정 HTTP 요청에 대한 보안 설정
//        return http
//                .authorizeRequests()
//                    .requestMatchers("/login", "/signup", "/user").permitAll()  // 이 경로에 대한 요청은 인증, 인가 없이 허용
//                    .anyRequest().authenticated()   // 그 외의 모든 요청은 인가는 필요 없지만 인증은 필요
//                .and()
//                .formLogin(formLogin -> formLogin
//                    .loginPage("/login")
//                    .defaultSuccessUrl("/articles")
//                )
//                .logout(logout -> logout
//                    .logoutSuccessUrl("/login")
//                    .invalidateHttpSession(true)    // 로그아웃 이후에 세션을 전체 삭제할지 여부
//                )
//                .csrf(AbstractHttpConfigurer::disable)  // AbstractHttpConfigurer 인터페이스의 disable() 메서드 참조
//                .build();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
//        // 사용자 인증 관련 설정
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//
//        daoAuthenticationProvider.setUserDetailsService(userService);   // 사용자 정보 가져올 서비스 설정
//        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());  // 비밀번호 인코딩 설정
//
//        return daoAuthenticationProvider;   // 설정 완료 후 반환
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//
//}
