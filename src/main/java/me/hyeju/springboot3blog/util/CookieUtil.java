package me.hyeju.springboot3blog.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 쿠키 관리 클래스

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // 쿠키 생성 (HTTP 응답 객체, 쿠키 이름, 쿠키 값, 쿠키 유효 기간)

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");    // 쿠키 경로를 루트로 설정해 모든 경로에서 접근 가능하도록 함
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie); // 쿠키를 응답에 추가
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        // 쿠키 삭제 (HTTP 요청 객체, HTTP 응답 객체, 삭제할 쿠키 이름)

        Cookie[] cookies = request.getCookies();    // 요청에서 쿠키 가져옴
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");    // 쿠키 값 비우기
                cookie.setPath("/");
                cookie.setMaxAge(0);    // 유효 기간 만료시키기

                response.addCookie(cookie); // 쿠키를 응답에 추가
            }
        }
    }

    public static String serialize(Object obj) {
        // 객체를 직렬화해 쿠키의 값으로 변환 (직렬화할 객체)

        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        // 쿠키를 역직렬화해 객체로 변환 (역직렬화할 쿠키, 역직렬화한 객체의 클래스 타입)

        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue()))
        );
    }

}
