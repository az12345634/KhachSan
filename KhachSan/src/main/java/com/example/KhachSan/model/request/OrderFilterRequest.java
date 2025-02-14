package com.example.KhachSan.model.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderFilterRequest {
    private Long orderId;

    private Long userId;

    private String code;
    private String username;
    private String productname;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
