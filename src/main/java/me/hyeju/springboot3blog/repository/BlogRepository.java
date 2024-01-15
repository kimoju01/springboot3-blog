package me.hyeju.springboot3blog.repository;

import me.hyeju.springboot3blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
