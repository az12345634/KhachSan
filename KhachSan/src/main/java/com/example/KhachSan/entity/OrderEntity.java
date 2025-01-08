package com.example.KhachSan.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "order_master")
public class OrderEntity extends AbstractEntity {
    // Mã đơn hàng
    private String code;

    // ID người dùng
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity userEntity;

    // Trạng thái đơn hàng
    private Integer status;

    // Địa chỉ ship đơn hàng
    @Column(name = "address_shipping")
    private String addressShipping;

    // Số điện thoại đặt đơn
    @Column(name = "phone_shipping")
    private String phoneShipping;

    // Tổng tiền đơn hàng
    @Column(name = "total_amount")
    private Double totalAmount;



}
