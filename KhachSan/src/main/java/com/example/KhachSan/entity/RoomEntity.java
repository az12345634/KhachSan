package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;

@Table(name = "room")
@Entity
@Data
public class RoomEntity extends AbstractEntity{
    private String name;
    private String description;
    private String code;
    @Column(name = "short_description")
    private String shortDescription;
    private Double price;
    private int quantity;
    private String image;

    private String address;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private CategoryEntity categoryEntity;


}
