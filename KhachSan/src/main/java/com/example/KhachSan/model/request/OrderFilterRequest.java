package com.example.KhachSan.model.request;

import lombok.Data;

@Data
public class OrderFilterRequest {
    private Long orderId;

    private Long userId;

    private String code;
    private String username;
    private String productname;
}
