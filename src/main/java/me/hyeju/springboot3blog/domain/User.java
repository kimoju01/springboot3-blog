package me.hyeju.springboot3blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 반환
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        // 사용자 패스워드 반환
        return password;
    }

    @Override
    public String getUsername() {
        // 사용자 id 반환(고유한 값)
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 반환
        return true;    // true => 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부 반환
        return true;    // true => 잠금되지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드 만료 여부 반환
        return true;    // true => 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        // 계정 사용 가능 여부 반환
        return true;    // true => 사용 가능
    }

}
