package me.hyeju.springboot3blog.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties("jwt")
public class JwtProperties {
    // application.properties에 있는 jwt 값들을 변수로 접근하기 위한 클래스

    private String issuer;
    private String secretKey;

}
