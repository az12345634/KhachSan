package com.example.KhachSan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Entity
@Table(name = "size")
@Data
public class SizeEntity extends AbstractEntity{

    private String code;

    private String name;

    private Integer weight;

    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "sizeEntities",fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    Set<ProductEntity> productEntities;

}
