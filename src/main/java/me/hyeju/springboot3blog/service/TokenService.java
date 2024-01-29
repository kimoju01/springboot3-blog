package me.hyeju.springboot3blog.service;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.config.jwt.TokenProvider;
import me.hyeju.springboot3blog.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 전달받은 리프레시 토큰으로 토큰 유효성 검사 진행하고 유효한 토큰이라면 리프레시 토큰으로 사용자 id를 찾음
        // 사용자 id를 찾은 뒤 genereateToken() 호출해서 새로운 액세스 토큰 생성

        if (!tokenProvider.validToken(refreshToken)) {  // 유효성 검사 실패 시 예외 발생
            throw new IllegalArgumentException("Unexpected token");
        }

        // 유효한 리프레시 토큰인 경우 아래 코드 수행
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));  // 새로운 액세스 토큰의 유효 시간은 2시간

    }

}
