package com.example.springauth.service;

import com.example.springauth.Entity.User;
import com.example.springauth.Entity.UserRoleEnum;
import com.example.springauth.dto.LoginRequestDto;
import com.example.springauth.dto.SignupRequestDto;
import com.example.springauth.jwt.JwtUtil;
import com.example.springauth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ADMIN_TOKEN 일반 사용자 인가 vs 관리자 인가
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    /**
     * 회원가입 로직
     * 1. username 의 중복 확인하기 db 에 unique 제약조건이 있어서 터지면 SQLException 임
     * 2. email 중복 확인하기 db 에 unique 제약조건이 있어서 터지면 SQLException 임
     * 3. 관리자로 등록하려면 관리자 token 을 기입해서 맞아야함 / 맞지 않다면 IllegalArgumentException
     * 이렇게 3가지 고려후 User user = new User(username, password, email, role); 패스워드는 encoding 한후 이것을 db 에 저장한다.
     */
    public void signup(SignupRequestDto requestDto) {
        log.info("signup start");
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());//encoding 암호화 하기

        log.info("username = {}", username);
        // 1. 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username); //만약 같은 username 을 가진 회원이 repository 에 존재한다면
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        // 2. email 중복확인
        String email = requestDto.getEmail();
        log.info("email = {}", email);
        Optional<User> checkEmail = userRepository.findByEmail(email); //여기서 문제라는 건데
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
        // 3. 사용자 ROLE 확인
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

//    /**
//     * Spring Security , Filter로 대체하여 비즈니스로직과, JWT 인증, 인가 로직 분리
//     */
//    public void login(LoginRequestDto requestDto, HttpServletResponse res) {
//        String username = requestDto.getUsername();
//        String password = requestDto.getPassword(); //평문 반환
//        //사용자 확인
//        User user = userRepository.findByUsername(username).orElseThrow(
//                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
//        );
//        //비밀번호 확인
//        if(!passwordEncoder.matches(password, user.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
//        }
//
//        // JWT 생성 및 쿠키에 저장 후 Response 객체에 추가하기
//        String token = jwtUtil.createToken(user.getUsername(), user.getRole()); //토큰 만들고
//        jwtUtil.addJwtToCookie(token, res); //그것을 응답 에 쿠키에추가
//    }
}