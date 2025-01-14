package com.example.KhachSan.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProductDTO {
    // ID
    private Long id;
    // Code
    private String code;
    // Tên
    private String name;
    // Giá Giá niêm yết
    private Double price;
    // Giá min
    private Double minPrice;
    // Giá max
    private Double maxPrice;
//    // Giá theo size đang active
//    private Double priceActive;
    // Mô tả
    private String description;
    // Mô tả ngắn
    private String shortDescription;
    // Url ảnh
    private String image;
    // Số lượng
    private int quantity;
    // Danh mục
    private String category;
    // Id danh mục
    private Long categoryId;
//    // Danh sách size
//    private Set<SizeDTO> sizes;
//    // Danh sách id size
//    private Set<Long> sizeIds;
//    // Id size active
//    private Long sizeActive;
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
