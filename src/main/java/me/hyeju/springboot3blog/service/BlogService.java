package me.hyeju.springboot3blog.service;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.domain.Article;
import me.hyeju.springboot3blog.dto.AddArticleRequest;
import me.hyeju.springboot3blog.dto.UpdateArticleRequest;
import me.hyeju.springboot3blog.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor    // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    // 전체 글 조회
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 글 하나 조회
    public Article findById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 글 삭제
    public void delete(Long id) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    // 글 수정
    @Transactional
    public Article update(Long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
        // 여기서 return blogRepository.save(article); 하지 않아도 업데이트 되는 이유? -> JPA 더티 체킹!
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        // 현재 인증 객체에 담겨져 있는 사용자의 정보와 글을 작성한 사용자의 정보 비교
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

}
