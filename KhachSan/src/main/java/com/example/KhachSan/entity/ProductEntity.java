package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Table(name = "product")
@Entity
@Data
public class ProductEntity extends AbstractEntity{
    private String name;
    private String description;
    private String code;
    @Column(name = "short_description")
    private String shortDescription;
    private Double price;
    private int quantity;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private CategoryEntity categoryEntity;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(name = "product_size",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private Set<SizeEntity> sizeEntities;
}
