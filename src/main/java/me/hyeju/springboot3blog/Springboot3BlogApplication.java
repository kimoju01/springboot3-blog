package me.hyeju.springboot3blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Springboot3BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot3BlogApplication.class, args);
    }

}
