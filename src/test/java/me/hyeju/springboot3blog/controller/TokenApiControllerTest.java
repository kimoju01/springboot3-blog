package me.hyeju.springboot3blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hyeju.springboot3blog.config.jwt.JwtFactory;
import me.hyeju.springboot3blog.config.jwt.JwtProperties;
import me.hyeju.springboot3blog.domain.RefreshToken;
import me.hyeju.springboot3blog.domain.User;
import me.hyeju.springboot3blog.dto.CreateAccessTokenRequest;
import me.hyeju.springboot3blog.repository.RefreshTokenRepository;
import me.hyeju.springboot3blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given: 테스트 유저 생성, 리프레시 토큰 만들어서 DB에 저장. 토큰 생성 API의 요청 본문에 리프레시 토큰을 포함해 요청 객체 생성
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build());

        // 리프레시 토큰 생성
        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));  // 생성한 리프레시 토큰을 DB에 저장

        // 토큰 생성 API에 전송할 요청 객체 생성하고 리프레시 토큰 설정
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);    // 요청 객체를 JSON 문자열로 변환

        // when: 토큰 추가 API에 요청 보냄. 요청 타입은 JSON, 요청 본문에는 요청 객체 함께 보냄
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then: 응답 코드 확인 후 응답으로 온 액세스 토큰이 비어있지 않은지 확인
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

}
