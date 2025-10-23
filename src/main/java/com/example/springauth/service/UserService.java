package com.example.springauth.service;

import com.example.springauth.Entity.User;
import com.example.springauth.Entity.UserRoleEnum;
import com.example.springauth.dto.SignupRequestDto;
import com.example.springauth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN_TOKEN 일반 사용자 인가 vs 관리자 인가
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto requestDto) {
        log.info("signup start");
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());//encoding 암호화 하기

        log.info("username = {}", username);
        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username); //만약 같은 username 을 가진 회원이 repository 에 존재한다면
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        // email 중복확인
        String email = requestDto.getEmail();
        log.info("email = {}", email);
        Optional<User> checkEmail = userRepository.findByEmail(email); //여기서 문제라는 건데
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) { //admin = true 라면
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN; //admin = true 임과 동시에, 관리자 암호도 맞다면,
        }

        // 사용자 등록
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }
}