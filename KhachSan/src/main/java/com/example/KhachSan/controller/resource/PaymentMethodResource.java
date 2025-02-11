package com.example.KhachSan.controller.resource;

import com.example.KhachSan.model.request.OrderFilterRequest;
import com.example.KhachSan.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
public class PaymentMethodResource {
    @Autowired
    private IPaymentService paymentService;
    @PostMapping("/vnpay")
    public ResponseEntity<?> getVnPay(@RequestBody OrderFilterRequest request) {
        return ResponseEntity.ok(paymentService.createVnPay(request));
    }
}
