package me.hyeju.springboot3blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    // 리프레시 토큰

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;    // 일련번호

    @Column(name = "user_id", nullable = false)
    private Long userId;    // 유저 id

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;    // 토큰 값

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }

}
