package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking")
public class BookingEntity extends AbstractEntity {

    // Người dùng đặt phòng
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private UserEntity userEntity;

    // Phòng/Sản phẩm được đặt
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private ProductEntity productEntity;

    // Ngày nhận phòng
    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;

    // Ngày trả phòng
    @Column(name = "check_out_date", nullable = false)
    private LocalDateTime checkOutDate;

    // Tổng tiền thanh toán
    @Column(name = "total_amount")
    private Double totalAmount;

    // Trạng thái đặt phòng (pending, confirmed, canceled)
    @Column(name = "status", length = 50)
    private String status;


}
