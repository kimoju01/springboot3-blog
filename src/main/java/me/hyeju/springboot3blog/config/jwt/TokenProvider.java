package me.hyeju.springboot3blog.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenProvider {
    // 토큰 생성하고 올바른 토큰인지 유효성 검사하고 토큰에서 필요한 정보를 가져오는 클래스

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        // 사용자 정보와 만료 시간으로 JWT 생성. makeToken() 메서드 호출해서 JWT 생성함
        Date now = new Date();  // now에는 현재 시간이 할당 됨. now.getTime()하면 now에 할당된 날짜와 시간의 밀리초 값을 반환
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user); // new Date(now.getTime()) 값은 now에 할당된 날짜, 시간과 같음!
    }

    private String makeToken(Date expiry, User user) {
        // JWT 생성하는 메서드
        Date now = new Date();

        return Jwts.builder()   // JWT 생성을 위한 빌더. JWT 헤더와 페이로드에 정보 설정
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)   // JWT 헤더) typ: JWT
                .setIssuer(jwtProperties.getIssuer())           // properties 파일에서 설정한 issuer 값
                .setIssuedAt(now)                               // JWT 페이로드) iat: 현재 시간
                .setExpiration(expiry)                          // 페이로드) exp: expiry 멤버 변수값
                .setSubject(user.getEmail())                    // 페이로드) sub: 유저 이메일
                .claim("id", user.getId())                // 페이로드) 클레임 id: 유저 ID
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())   // 서명) 비밀키와 함께 해시값을 HS256 방식으로 암호화
                .compact(); // JWT 문자열 생성
    }

    public boolean validToken(String token) {
        // JWT 유효성 검증 메서드
        try {
            Jwts.parser()   // JWT 파싱을 위한 파서 만듦
                    .setSigningKey(jwtProperties.getSecretKey())    // Properties에 선언한 비밀 키로 토큰 복호화 시도
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 복호화에서 에러 발생 => 유효하지 않은 토큰이므로 false 반환
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        // JWT로부터 인증 정보 가져오는 메서드. 인증 정보를 담은 객체 Authentication 반환
        // 비밀 키로 토큰을 복호화한 뒤 클레임 가져오는 getClaims() 메서드 호출해서
        // 클레임 정보 반환받아 사용자 이메일이 들어 있는 토큰 제목 sub와 토큰 기반으로 인증 정보 생성

        Claims claims = getClaims(token);
        // Spring Security에서 권한 나타내는 객체(SimpleGrantedAuthority)를 생성해서 Collection.singleton 메서드로 이 권한 포함하는 Set 생성
        // authorities 라는 이름의 Set 객체(중복 허용X)에 ROLE_USER 권한을 추가
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        // User는 내가 만든 User 엔티티가 아니고 스프링 시큐리티에서 제공하는 User 클래스임
        // UsernamePasswordAuthenticationToken? Spring Security에서 인증 객체를 나타냄. 성공적으로 로그인한 경우 인증된 사용자로써의 정보 나타냄
        // claims.getSubject()? 클레임 중 sub에 해당하는 값(사용자의 식별 정보. ex.이메일) 가져옴
        // ""? JWT 토큰을 사용해 인증을 수행하고 있기 때문에 비밀번호는 사용되지 않음. 따라서 "" 이나 null으로 설정
        // authorities? 사용자의 권한 정보를 담은 Set
        // token? 인증 성공 시 생성된 토큰
        // authorities? 다시 한 번 권한 정보를 전달
        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    public Long getUserId(String token) {
        // 토큰 기반으로 유저 ID를 가져오는 메서드
        // getClaims() 메서드 호출해서 클레임 정보 반환받고 id 키로 저장된 값을 가져와 반환
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        // 클레임 추출 메서드
        return Jwts.parser()    // JWT 파싱을 위한 파서 만듦
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();     // JWT 페이로드(클레임) 가져옴
    }

}
