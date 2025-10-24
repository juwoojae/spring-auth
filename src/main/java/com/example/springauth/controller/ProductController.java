package com.example.springauth.controller;

import ch.qos.logback.core.model.Model;
import com.example.springauth.Entity.User;
import com.example.springauth.Entity.UserRoleEnum;
import com.example.springauth.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class ProductController {
    /**
     * Authentication 의 Principal 에 저장된 UserDetailsImpl 를 가지고 올수 있다.
     */
    @GetMapping("/products")
    public String getProducts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Authentication 의 Principal 에 저장된 UserDetailsImpl 을 가져옵니다.
        log.info("getProducts");
        User user =  userDetails.getUser();
        System.out.println("user.getUsername() = " + user.getUsername());

        return "redirect:/";
    }

    /**
     *
     */
    @Secured(UserRoleEnum.Authority.ADMIN) // 관리자용
    @GetMapping("/products/secured")
    public String getProductsByAdmin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        System.out.println("userDetails.getUsername() = " + userDetails.getUsername());
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            System.out.println("authority.getAuthority() = " + authority.getAuthority());
        }

        return "redirect:/";
    }
}