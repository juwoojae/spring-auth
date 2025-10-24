package com.example.springauth.config;


import com.example.springauth.jwt.JwtAuthenticationFilter;
import com.example.springauth.jwt.JwtAuthorizationFilter;
import com.example.springauth.jwt.JwtUtil;
import com.example.springauth.security.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableMethodSecurity(securedEnabled = true) // @Secured 애너테이션 활성화
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * authenticationManager 는 AuthenticationConfiguration을 통해서 생성할수있다
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));//JwtAuthenticationFilter 가 authenticationManager 를 사용하므로 세팅해줘야함
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }
    //여기까지 필터 생성을 위한 빈등록 의존관계주입, 필터 스프링 빈 등록
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        http.formLogin((formLogin) ->
                formLogin
                        .loginPage("/api/user/login-page").permitAll()
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); //jwtAuthorizationFilter ->  JwtAuthenticationFilter -> .. 순으로 실행
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //이렇게 써도 UsernamePasswordAuthenticationFilter.class 는 실행 안됨.

        //접근 불가 페이지 설정
        http.exceptionHandling((exceptionHandling) ->exceptionHandling.accessDeniedPage("/forbidden.html"));

        return http.build();
    }
}
/**
 * 스프링 시큐리티에서 기본으로 제공하는 로그인 폼(formLogin) 은 세션(Session) 을 사용해서 인증 상태를 유지
 *  1. 로그인 요청일때
 *  (1) JwtAuthorizationFilter
 *  토큰이 없으므로 인증(SecurityContext 세팅) 패스
 *  doFilter()로 다음 필터로 진행
 *  (2) JwtAuthenticationFilter
 *  로그인 요청 -> id/pw 검증후 (DetailsService로) JWT 발행
 *  JWT 를 쿠키나 응답 헤더에 담아 클라이언트로 전달
 *
 *  2. 로그인 후 요청하기(JWT 포함)
 *  (1) JwtAuthorizationFilter
 *  JWT 검증 → 유효하면 SecurityContext에 인증(Authentication) 객체 세팅
 *  SecurityContext에 인증 정보가 들어있으므로 인가(Authorization) 로직 적용 가능
 *  (2) JwtAuthenticationFilter
 *  로그인 요청이 아니므로 실행되지 않음
 */