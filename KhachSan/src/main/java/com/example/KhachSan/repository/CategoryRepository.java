package com.example.KhachSan.repository;

import com.example.KhachSan.entity.CategoryEntity;
import com.example.KhachSan.model.request.CategoryFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {

    @Query(value = "SELECT DISTINCT o FROM CategoryEntity o " +
            "LEFT JOIN o.productEntities u " +
            " WHERE " +
            "(:#{#condition.productId} is null or u.id = :#{#condition.productId} )" +
            "AND o.deleted=false ORDER BY o.createdDate desc "
    )

    Page<CategoryEntity> findAllByFilter(@Param("condition") CategoryFilterRequest filterRequest, Pageable pageable);
}
