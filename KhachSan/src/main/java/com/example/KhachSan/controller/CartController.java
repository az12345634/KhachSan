package com.example.KhachSan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views/cart")
public class CartController {
    @GetMapping()
    public String product(Model model) {
        model.addAttribute("pageTitle", "Đặt phòng");
        return "shop_cart";
    }
}
