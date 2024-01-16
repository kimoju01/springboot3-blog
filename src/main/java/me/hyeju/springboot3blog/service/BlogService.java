package me.hyeju.springboot3blog.service;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.dto.AddArticleRequest;
import me.hyeju.springboot3blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor    // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 전체 글 조회
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

}
