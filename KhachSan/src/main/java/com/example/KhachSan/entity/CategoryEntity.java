package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Table(name = "category")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity extends AbstractEntity {

    private String name;

    private String code;

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ProductEntity> productEntities;
}
