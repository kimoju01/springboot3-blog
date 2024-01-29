package me.hyeju.springboot3blog.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Getter
public class JwtFactory {
    // JWT 테스트할 때 쓸 mocking 객체

    private String subject = "test@email.com";
    private Date issuedAt = new Date(); // 토큰이 발급된 시간
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());  // 토큰 만료 시간은 발급된 시간에서 14일 후
    private Map<String, Object> claims = Collections.emptyMap();    // 토큰에 추가할 클레임을 담을 Map

    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues() {
        // 기본 값을 사용해 JwtFactory 객체 생성하는 메서드
        return JwtFactory.builder().build();
    }

    public String createToken(JwtProperties jwtProperties) {
        // jjwt 라이브러리를 사용해 JWT 생성
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

}
