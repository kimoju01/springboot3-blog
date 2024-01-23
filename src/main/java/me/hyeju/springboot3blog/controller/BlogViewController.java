package me.hyeju.springboot3blog.controller;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.dto.ArticleListViewResponse;
import me.hyeju.springboot3blog.dto.ArticleViewResponse;
import me.hyeju.springboot3blog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleListViewResponse::new)
                .toList();

        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);

        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {    // 쿼리 파라미터 값을 id 변수에 매핑. id가 없을 수도 있음
        if (id == null) {   // id가 없으면 글 생성
            model.addAttribute("article", new ArticleViewResponse());   // 기본 생성자로 비어있는 ArticleViewResponse 객체 만듦
        } else {    // id가 있으면 글 수정
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }

}
