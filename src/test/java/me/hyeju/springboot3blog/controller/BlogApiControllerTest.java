package me.hyeju.springboot3blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.domain.User;
import me.hyeju.springboot3blog.dto.AddArticleRequest;
import me.hyeju.springboot3blog.dto.UpdateArticleRequest;
import me.hyeju.springboot3blog.repository.BlogRepository;
import me.hyeju.springboot3blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc   // MockMvc 생성
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;    // 직렬화(자바객체->JSON), 역직렬화(JSON->자바객체)를 위함

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach // 테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        blogRepository.deleteAll();
    }

    @BeforeEach
    void setSecurityContext() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // SecurityContext에 setAuthentication()으로 테스트 유저 지정
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        // 객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // Principal 객체에 테스트 유저가 들어가도록 모킹
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        // when
        // MockMvc를 사용해 HTTP 메서드, URL, 요청 본문, 요청 타입 설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .principal(principal)
                .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();
        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        //given: 블로그 글 저장
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        //when: 목록 조회 API 호출
        ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then: 응답 코드가 200 OK인지, 저장된 값 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()));

    }

    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // given: 블로그 글 저장
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));

        // then: 응답 코드가 200 OK 인지, 저장된 값 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        // given: 블로그 글 저장
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        // when
        mockMvc.perform(delete(url, savedArticle.getId())).andExpect(status().isOk());

        // then
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle() throws Exception {
        // given: 블로그 글 저장, 수정 요청 객체 생성
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        // when
        ResultActions resultActions = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then: 응답 코드, 값 검증
        resultActions.andExpect(status().isOk());
        Article article = blogRepository.findById(savedArticle.getId()).get();
        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        // 글 만들기

        return blogRepository.save(Article.builder()
                .title("title")
                .author(user.getUsername())
                .content("content")
                .build());
    }

}