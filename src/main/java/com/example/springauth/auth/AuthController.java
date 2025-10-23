package com.example.springauth.auth;

import com.example.springauth.Entity.UserRoleEnum;
import com.example.springauth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtUtil jwtUtil;


    /**
     * 쿠키를 만드는 메서드
     */

    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        addCookie("Robbie Auth", res);

        return "createCookie";
    }

    /**
     * 쿠키를 가지고 오는 메서드
     * HTTPServletRequest 에 들어있는 쿠키중 AUTHORIZATION_HEADER 의 name 을 가진 쿠키를
     * 에노테이션이 자동으로  쿠키의 값 (value) 를 가지고온후 매핑해준다.
     */

    @GetMapping("/get-cookie")
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) {

        System.out.println("value = " + value);

        return "getCookie : " + value;
     }

    @GetMapping("/create-jwt")
    public String createJwt(HttpServletResponse res) {
        // Jwt 생성
        String token = jwtUtil.createToken("Robbie", UserRoleEnum.USER);

        // Jwt 쿠키 저장
        jwtUtil.addJwtToCookie(token, res);

        return "createJwt : " + token;
    }

    /**
     * jwt 생성 및 조회
     */
    @GetMapping("/get-jwt")
    public String getJwt(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) { //쿠키에서 해당 header key(쿠키 이름) 으로 value 가지고 오기
        // JWT 토큰 substring
        String token = jwtUtil.substringToken(tokenValue); //앞에 Bearer 제외하기

        // 토큰 검증
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("Token Error");
        }
        /**
         * JWT 는 3가지 부분으로 가지고있다
         * Header 토큰의 메타데이터(알고리즘, 타입)
         * Payload 실제 데이터 (Claims)
         * Signature 토큰의 무결성을 검증하기 위한 서명
         * 즉 Payload == Claims 이다
         */
        // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // 사용자 username
        String username = info.getSubject();
        System.out.println("username = " + username);
        // 사용자 권한
        String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
        System.out.println("authority = " + authority);

        return "getJwt : " + username + ", " + authority;
    }

    @GetMapping("/create-session")
    public String createSession(HttpServletRequest req) {
        // create : true 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
        HttpSession session = req.getSession(true);

        // 세션에 저장될 정보 Name - Value 를 추가합니다.
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

        return "createSession";
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // create : false 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false);

        String value = (String) session.getAttribute(AUTHORIZATION_HEADER); // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져온다.
        System.out.println("value = " + value);

        return "getSession : " + value;
    }

    /**
     * 쿠키를 만들때 사용하는 헬퍼 메서드
     */

    public static void addCookie(String cookieValue, HttpServletResponse res) {
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name, Value (서블릿이 제공하는 쿠키 클래스)
            cookie.setPath("/");  //해당 경로 밑으로는 다 클라이언트의 쿠키가 적용된다
            cookie.setMaxAge(30 * 60);

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
