package com.example.springauth.controller;

import ch.qos.logback.core.model.Model;
import com.example.springauth.Entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProductController {

    /**
     *@AuthenticationPrincipal 는 SecurityController 안에 저장된 Authentication 객체를
     * 자동으로 꺼내서 그안의 principal 주체 , 즉 로그인한 사용자 정보를 구입해주는 기능
     */
    @GetMapping("/products")
    public String getProducts(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userDetails.getUser();

        return "redirect:/";
    }

}