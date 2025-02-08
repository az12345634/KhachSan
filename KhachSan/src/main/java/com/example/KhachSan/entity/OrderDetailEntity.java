package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "order_detail")
@Data
public class OrderDetailEntity extends AbstractEntity {
    // ID đơn hàng
    @ManyToOne
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private OrderEntity orderEntity;

    // ID sản phẩm
    @OneToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ProductEntity productEntity;


    // Số lượng sản phẩm
    private Integer quantity;

    // Tổng tiền
    private Double total;
}
