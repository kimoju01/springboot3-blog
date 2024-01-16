package me.hyeju.springboot3blog.controller;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.dto.AddArticleRequest;
import me.hyeju.springboot3blog.dto.ArticleResponse;
import me.hyeju.springboot3blog.service.BlogService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController     // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러(Controller + ResponseBody)
@RequiredArgsConstructor    // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("/api/articles")
    // ResponseEntity로 클라이언트에게 전송할 응답의 상태 코드, 헤더, 본문 등을 설정할 수 있음
    // @RequestBody는 요청할 때 본문을 자바 객체로 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);

        // 응답코드를 201로 설정하고 저장된 Article 객체를 응답 본문에 담아 클라이언트에 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        // 스트림으로 ArticleResponse 객체로 변환한 뒤 List로 수집
        // ArticleResponse 생성자 참조해서 Article을 ArticleResponse로 매핑
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));  // 생성자로 Article 객체 받아서 ArticleResponse 객체로 변환
    }

}
