package com.example.KhachSan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDetailDTO {
    // ID
    private Long id;
    // Ngày tạo
    private LocalDateTime createDate;
    // Tên tài khoản tạo
    private String createBy;
    // Ngày chỉnh sửa gần nhất
    private LocalDateTime lastModifiedDate;
    // Người chỉnh sửa
    private String lastModifiedBy;
    // Deleted
    private Boolean deleted;
    // ID sản phẩm
    private Long productId;
    // ID size
    private Long sizeId;
    // Tổng tiền
    private Double total;
    // Số lượng sản phẩm
    private Integer quantity;
    // ID đơn hàng
    private Long orderId;
    // Tên sản phẩm
    private String productName;
    // Tên size
    private String sizeName;

    private String productCode;

}
