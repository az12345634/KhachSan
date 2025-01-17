package com.example.KhachSan.repository;

import com.example.KhachSan.entity.ProductEntity;
import com.example.KhachSan.model.request.ProductFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "SELECT DISTINCT p FROM ProductEntity p " +
            "LEFT JOIN p.categoryEntity c " +
            " WHERE " +
            " (:#{#condition.name} is null or lower(p.name) = :#{#condition.name}) " +
            "AND (:#{#condition.code} is null or p.code = :#{#condition.code} )" +
            "AND (:#{#condition.price} is null or p.price = :#{#condition.price} )" +
            "AND (:#{#condition.categoryId} is null or c.id = :#{#condition.categoryId} )" +
            "AND p.deleted=false ORDER BY " +
            "CASE WHEN :#{#condition.created} = 'asc' THEN p.createdDate END ASC, " +
            "CASE WHEN :#{#condition.created} = 'desc' THEN p.createdDate END DESC," +
            "CASE WHEN :#{#condition.byPrice} = 'asc' THEN p.price END ASC, " +
            "CASE WHEN :#{#condition.byPrice} = 'desc' THEN p.price END DESC"
    )
    Page<ProductEntity> findAllByFilter(@Param("condition") ProductFilterRequest filterRequest, Pageable pageable);

    @Query(value = "SELECT p FROM ProductEntity p " +
            "LEFT JOIN p.categoryEntity c " +
            " WHERE " +
            " (:#{#condition.code} is null or lower(p.code) = :#{#condition.code}) " +
            "AND p.deleted=false ORDER BY p.createdDate desc LIMIT 1"
    )
    ProductEntity findByFilter(@Param("condition") ProductFilterRequest filterRequest);

    @Query(value = "SELECT p FROM ProductEntity p " +
            "LEFT JOIN p.categoryEntity c " +
            " WHERE " +
            " (:#{#code} is null or lower(p.code) = :#{#code}) " +
            "AND p.deleted=false ORDER BY p.createdDate desc LIMIT 1"
    )
    ProductEntity findByCode(String code);

    @Query("SELECT nv FROM ProductEntity nv WHERE nv.name LIKE %:keyword% OR nv.code LIKE %:keyword%")
    List<ProductEntity> searchBySearch(@Param("keyword") String keyword);
}