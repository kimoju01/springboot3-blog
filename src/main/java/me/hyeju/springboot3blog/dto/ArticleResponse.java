package me.hyeju.springboot3blog.dto;

import lombok.Getter;
import me.hyeju.springboot3blog.domain.Article;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(Article article) {   // 생성자. 파라미터에 String title, String content 대신 Article 객체 받음
        // Article 객체 받아서 ArticleResponse 객체 생성. 왜 Article 객체? 데이터 전달, 추출이 편하니까!
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
