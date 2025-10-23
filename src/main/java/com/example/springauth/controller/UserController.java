package com.example.springauth.controller;

import com.example.springauth.dto.SignupRequestDto;
import com.example.springauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }
    //회원가입 페이지 호출
    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }
    //회원가입
    @PostMapping("/user/signup")
    public String signup(@ModelAttribute SignupRequestDto requestDto) {
        System.out.println(requestDto.getUsername());
        System.out.println(requestDto.getPassword());
        System.out.println(requestDto.getEmail());
        userService.signup(requestDto);

        return "redirect:/";
    }
}