package com.example.KhachSan.repository;

import com.example.KhachSan.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    @Query(value = "SELECT od FROM OrderDetailEntity od " +
            "LEFT JOIN od.productEntity p " +
            " WHERE " +
            " (:#{#orderId} is null or od.orderEntity.id = :#{#orderId}) " +
            "AND p.deleted=false"
    )
    List<OrderDetailEntity> findByCode(Long orderId);
    @Query(value = "SELECT o FROM OrderDetailEntity o " +
            "LEFT JOIN o.orderEntity u " + // Thay đổi userEntity thành user
            "LEFT JOIN o.productEntity p " +
            "WHERE " +
            "(:#{#userId} is null or u.userEntity.id = :#{#userId})" +
            " AND (:#{#productId} is null or p.id = :#{#productId} )" +

//            "AND (+:#{#productId.name} IS NULL OR p.name = :#{#filter.name})" +
            "AND o.deleted=false ORDER BY o.createdDate desc ")
    OrderDetailEntity findOrderEntitiesBy(Long userId, Long productId);


}
