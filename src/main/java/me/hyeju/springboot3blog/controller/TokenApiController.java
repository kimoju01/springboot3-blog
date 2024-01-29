package me.hyeju.springboot3blog.controller;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.dto.CreateAccessTokenRequest;
import me.hyeju.springboot3blog.dto.CreateAccessTokenResponse;
import me.hyeju.springboot3blog.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        // 리프레시 토큰으로 새로운 액세스 토큰을 생성
        // @RequestBody로 요청 본문에 있는 데이터를 CreateAccessTokenRequest 객체로 매핑

        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newAccessToken));
    }

}
