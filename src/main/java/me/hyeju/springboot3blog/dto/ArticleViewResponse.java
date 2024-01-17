package me.hyeju.springboot3blog.dto;

import lombok.Getter;
import me.hyeju.springboot3blog.domain.Article;

import java.time.LocalDateTime;

@Getter
public class ArticleViewResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
    }

}
