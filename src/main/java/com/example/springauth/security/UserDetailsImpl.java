package com.example.springauth.security;

import com.example.springauth.Entity.User;
import com.example.springauth.Entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring Security 내부에서 Authentication 생성
 * 로그인 성공 시, UsernamePasswordAuthenticationToken 이 만들어 진다.
 * new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); <- 인증 + 인가 로직
 */
public class UserDetailsImpl implements UserDetails {

    private final User user; //DB 에서 가지고온 User 엔티티 객체

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     *  getAuthorities() 는 Collection< ? extends GrantedAuthority> 를 반환해야 한다
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = user.getRole();
        String authority = role.getAuthority(); //권한 이름의 규칙 ROLE_ 로 시작해야함.

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority); //SimpleGrantedAuthority 는 Spring Security 에서 권한(Authority/Role) 을 나타내는 객체
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
