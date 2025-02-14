package com.example.KhachSan.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views/product")
public class RoomController {
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("pageTitle", "Sản phẩm");
        return "room";
    }

    @GetMapping("/detail/{code}")// link này chuyển trang trong layout dùng chung
    public String productDetail(Model model) {
        model.addAttribute("pageTitle", "Chi tiết sản phẩm");

        return "room_detail"; //  này là chỉ tới trang giao diên html
    }



}
