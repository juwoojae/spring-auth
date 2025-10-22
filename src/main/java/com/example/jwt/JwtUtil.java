package com.example.jwt;

import com.example.Entity.UserRoleEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Util 는 특정 파라메터에 대한 작업을 수행하는 메서드들을 모아둔 클래스.
 * 다른 객체에 의존하지 않는다.
 * ex) String $ 로 반환
 */

public class JwtUtil {
    // Header KEY 값

    public static final String AUTHORIZATION_HEADER = "Authorization";//쿠키의 name 값
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer "; // 규칙 토큰 앞에 붙이는것
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    /**
     * application.properties 에서 설정한 key 값으로 SecretKey 의 value 를 가지고 온다
     */
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    private Key key; //@PostConstruct 에서 초기화

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }
    //여기까지 데이터 준비코드

    // JWT 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact(); //리턴값은 String
    }

    // 생성된 JWT 를 Cookie 에 저장

    // Cookie 에 들어있던 JWT 토큰을 Substring

    // JWT 검증

    // JWT 에서 사용자 정보 가지고 오기
}
