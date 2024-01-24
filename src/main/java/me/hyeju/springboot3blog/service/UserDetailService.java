package me.hyeju.springboot3blog.service;

import lombok.RequiredArgsConstructor;
import me.hyeju.springboot3blog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 이름(email)로 사용자 정보 찾아서 UserDetails 객체 반환.
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(("User not found: " + email)));
    }

}
