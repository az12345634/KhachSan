package com.example.KhachSan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SizeDTO {
    // ID
    private Long id;
    // Tên size
    private String name;
    // Khối lượng
    private Integer weight;
    // Mã size
    private String code;
    // Deleted
    private Boolean deleted;
    // Ngày tạo
    private LocalDateTime createDate;
    // Tên tài khoản tạo
    private String createBy;
    // Ngày chỉnh sửa gần nhất
    private LocalDateTime lastModifiedDate;
    // Người chỉnh sửa
    private String lastModifiedBy;
}
