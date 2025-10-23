package com.example.springauth.jwt;

import com.example.springauth.Entity.UserRoleEnum;
import com.example.springauth.dto.LoginRequestDto;
import com.example.springauth.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * 인증 Authentication 필터
 * 로그인을 했을때 JWT 를 생성하는게 맞지
 */

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login"); //로그인 처리 (Post /api/user/login)
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {//json 형태의 데이터를 객체로 직렬화하기
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            //AuthenticationManager 의 authenticate() 메서드 (username, password) 를 가지고와서 인증 처리하기
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )  //Authentication 객체를 만든 후에 리턴
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Authentication 를 파라메터로 받아온다.
     * @AuthenticationPrincipal UserDetailsImpl userDetails 멘치로 Authentication 에서 getPrincipal 을 한다
     * Controller 에서는 UserDetailsService 에서 UserDetails 를 가지고 왔지만, Filter 에서는 DispatcherServlet 전 단계이므로
     * 에노테이션이 아니라 (UserDetailsImpl) authResult.getPrincipal()) 이런식으로 가지고 온다.
     */

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername(); //Authentication 에서 username 가지고 오기
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole(); //Authentication 에서 role 가지고 오기

        String token = jwtUtil.createToken(username, role); //토큰 생성후
        jwtUtil.addJwtToCookie(token, response);  //쿠키에 넣기
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}