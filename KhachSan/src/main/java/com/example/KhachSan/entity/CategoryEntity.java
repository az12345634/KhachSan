package com.example.KhachSan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference // Ngăn vòng lặp khi tuần tự hóa JSON
    @ToString.Exclude
    private List<RoomEntity> roomEntities;
}
