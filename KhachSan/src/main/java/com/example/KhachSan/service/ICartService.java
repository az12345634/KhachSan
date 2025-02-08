package com.example.KhachSan.service;

import com.example.KhachSan.model.dto.CartDTO;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;

public interface ICartService {
    BaseResponse<Page<CartDTO>> getAll(int page, int size);

    BaseResponse<?> addToCart(CartDTO cartDTO);

    BaseResponse<Long> countCart();

    BaseResponse<?> deleteCart(CartDTO cartDTO);
}
