package com.example.springauth.security;
import com.example.springauth.Entity.User;
import com.example.springauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        return new UserDetailsImpl(user);
    }
}
/**
 * SecurityFilterChain
 * client 가 username 과 password 를 제출하면 UsernamePasswordAuthenticationFilter 는
 * 인증된 사용자의 정보가 담기는 인증 객체인 Authentication 의 종류중 하나인
 * UsernamePasswordAuthenticationToken 을 만들어 AuthenticationManager 에게 넘겨서 인증을 시도한다
 * 성공할경우 SecurityContextHolder 에 Authentication 를 세팅한다
 *
 * 스프링 시큐리티에서 UserDetailsImpl 과 UserDetailsServiceImpl
 * 1. UserDetailsImpl 과 UserDetailsServiceImpl 인터페이스를 구현해서 사용한다는것은
 * Spring Security 가 제공하ㅏ는 default 기능을 사용하지 않겠다는 설정이 된다. (security 의 password를 더이상 사용하지 않는다)
 *
 * 2. 기능
 * 사용자가 로그인 요청을 보낸다 /login 으로 username, password 를 전송
 * UsernamePasswordAuthenticationFilter 가 요청을 가로챈다. 요청에서 username, password 를 가로챈다
 * AuthenticationManager 에게 전달해서 AuthenticationProvider 로 인증을 위임하는데 , 사용자 정보를 DB 에서 가지고 올때
 * UserDetailService 를 사용한다
 * DB 에서 User 정보를 가지고오는 비즈니스 로직이다
 * username 으로 UserDetails 을 가지고 오는 역할 이다. 스프링 시큐리티는 사용자 정보를 UserDetails 형태로 다룬다, 즉, DB 의 User 엔티티를 그대로 사용하지 않고,DTO 형식으로 감싸서 회원정보를 가지고 온다
 *AuthenticationManager 가 받아온 평문 password 를 암호화한뒤에 UserDetails의 password 와 비교 한뒤
 * 인증에 성공하는 경우
 * 이것을 Security Context 에 넣어둔다.
 * 실패하는 경우 Error 발생시킨다.
 */