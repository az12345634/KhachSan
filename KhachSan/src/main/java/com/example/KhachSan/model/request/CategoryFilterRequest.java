package com.example.KhachSan.model.request;

import lombok.Data;

@Data
public class CategoryFilterRequest {
    private String name;
    private String code;
    private Long productId;
}
