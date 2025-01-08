package com.example.KhachSan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views")
public class HomeController {
    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Trang chủ");
        return "login";
    }

//    @GetMapping("/blog")
//    public String adminBlog(Model model) {
//        model.addAttribute("pageTitle", "Giới thiệu");
//        return "/blog";
//    }

}
