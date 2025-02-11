package com.example.KhachSan.service;

import com.example.KhachSan.model.request.OrderFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;

public interface IPaymentService {
    BaseResponse<String> createVnPay(OrderFilterRequest request);
}
