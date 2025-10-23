package com.example.springauth.controller;

import com.example.springauth.dto.LoginRequestDto;
import com.example.springauth.dto.SignupRequestDto;
import com.example.springauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final HandlerMapping resourceHandlerMapping;

    public UserController(UserService userService, HandlerMapping resourceHandlerMapping) {
        this.userService = userService;
        this.resourceHandlerMapping = resourceHandlerMapping;
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

        return "redirect:/api/user/login-page";
    }

    @PostMapping("/user/login")
    public String login(LoginRequestDto requestDto, HttpServletResponse res){
        try {
            userService.login(requestDto, res);
        } catch (Exception e) {
            return "redirect:/api/user/login-page?error"; //로그인 페이지로 리다이렉트
        }

        return "redirect:/";
    }
}