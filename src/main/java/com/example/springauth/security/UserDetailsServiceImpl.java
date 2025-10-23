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
 * 스프링 시큐리티에서 UserDetailsImpl 과 UserDetailsServiceImpl
 * 사용자가 로그인 요청을 보낸다 /login 으로 username, password 를 전송
 * UsernamePasswordAuthenticationFilter 가 요청을 가로챈다. 요청에서 username, password 를 가로챈다
 * AuthenticationManager 에게 전달해서 AuthenticationProvider 로 인증을 위임하는데 , 사용자 정보를 DB 에서 가지고 올때
 * UserDetailService 를 사용한다
 * public interface UserDetailsService {
 *     UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
 * } 이렇게 하는의 메서드만 존재한다
 * username 으로 UserDetails 을 가지고 오는 역할 이다. 스프링 시큐리티는 사용자 정보를 UserDetails 형태로 다룬다, 즉, DB 의 User 엔티티를 그대로 사용하지 않고,DTO 형식으로 감싸서 회원정보를 가지고 온다
 *AuthenticationManager 가 받아온 평문 password 를 암호화한뒤에 UserDetails의 password 와 비교 한뒤
 * 인증에 성공하는 경우
 * 이것을 Security Context 에 넣어둔다.
 * 실패하는 경우 Error 발생시킨다.
 */