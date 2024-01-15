package me.hyeju.springboot3blog.controller;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.dto.AddArticleRequest;
import me.hyeju.springboot3blog.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
