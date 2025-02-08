package com.example.KhachSan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartDTO {
    // ID
    private Long id;
    // Ngày tạo
    private LocalDateTime createdDate;
    // Username tạo
    private String createdBy;
    // Id user
    private Long userId;
    // Id sản phẩm
    private Long productId;
    // Tổng giá trị đơn hàng
    private Double totalAmount;
    // Sản phẩm
    private ProductDTO product;
    // Tên sản phẩm
    private String productName;
    // Url ảnh sản phẩm
    private String imgProduct;
    // Số lượng
    private Integer quantity;
    // Giá sản phẩm
    private Double productPrice;
    // Tổng ( kg của size * giá sản phẩm * số lượng )
    private Double totalOneP;

    private String productCode;
}
