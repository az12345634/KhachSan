package com.example.KhachSan.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {
    @GetMapping()
    public String product(Model model) {
        model.addAttribute("adminTitle", "Danh sách sản phẩm");
        return "admin/product";
    }
    @GetMapping("/new")
    public String addProduct(Model model){
        model.addAttribute("adminTitle", "Thêm mới sản phẩm");
        return "admin/add_product";
    }

    @GetMapping("/update/{code}")
    public String updateProduct(Model model){
        model.addAttribute("adminTitle", "Cập nhật sản phẩm");
        return "admin/update_product";
    }
}
