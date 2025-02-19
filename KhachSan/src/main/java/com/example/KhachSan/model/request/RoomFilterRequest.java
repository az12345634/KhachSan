package com.example.KhachSan.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RoomFilterRequest {
    private String name;
    private String code;
    private Float price;
    private String byPrice;
    private Long sizeId;
    private Integer categoryId;
    private String created;
    private String address;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;
}
