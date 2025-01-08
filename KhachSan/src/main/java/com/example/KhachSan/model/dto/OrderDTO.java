package com.example.KhachSan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    // ID
    private Long id;
    // Mã đơn hàng
    private String code;
    // Id người dùng
    private Long userId;
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
    // Trạng thái đơn hàng
    private Integer status;
    // Địa chỉ giao hàng
    private String addressShipping;
    // Số điện thoại giao hàng
    private String phoneShipping;
    // Tổng số tiền đơn hàng
    private Double totalAmount;
    private String username;
private String productname;
    // Chi tiết đơn hàng
    private List<OrderDetailDTO> orderDetails;
}
