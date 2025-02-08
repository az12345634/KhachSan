package com.example.KhachSan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views/order")
public class OrderController {


    @GetMapping("/place-order/{code}")// link này chuyển trang trong layout dùng chung
    public String shop(@PathVariable("code") String code, Model model) {
        model.addAttribute("pageTitle", "Thanh toán đơn hàng");

        return "place_order"; //  này là chỉ tới trang giao diên html
    }

    @GetMapping()
    public String listOrder() {
        return "order";
    }

    @GetMapping("/payment/result/{code}")
    public String resultPayment(@PathVariable String code) {
        return "result_payment";
    }

    @GetMapping("/detail/{code}")
    public String detailOredr(@PathVariable String code) {
        return "order_detail";
    }

}
