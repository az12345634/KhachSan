package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class AbstractEntity {
    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ngày tạo
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // Tên tài khoản đã tạo
    @Column(name = "created_by")
    private String createdBy;

    // Ngày sửa đổi
    @Column(name = "modified_date")
    private LocalDateTime lastModifiedDate;

    // Tên tài khoản sửa đổi
    @Column(name = "modified_by")
    private String lastModifiedBy;

    // Trạng thái xóa
    private Boolean deleted = false;
}
