package me.hyeju.springboot3blog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;

@Getter
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author) { // 생성자를 사용해 객체 생성. DTO -> Entity로 변환하는 메서드.
        // 블로그에 글 추가할 때 저장할 엔티티로 변환하는 용도
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }

}
