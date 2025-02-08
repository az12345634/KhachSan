package com.example.KhachSan.repository;

import com.example.KhachSan.entity.OrderEntity;
import com.example.KhachSan.model.request.OrderFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = "SELECT o FROM OrderEntity o " +
            "LEFT JOIN o.userEntity u " + // Thay đổi userEntity thành user
            "LEFT JOIN o.orderDetailEntities d " +
            "WHERE " +
            "(:#{#condition.userId} is null or u.id = :#{#condition.userId})" +
            " AND (:#{#condition.username} is null or u.username = :#{#condition.username} )" +
            " AND (:#{#condition.orderId} is null or o.id = :#{#condition.orderId} )" +
            " AND (:#{#condition.code} is null or lower(o.code)  = :#{#condition.code} )" +
            "AND o.deleted=false ORDER BY o.createdDate desc "
    )
    Page<OrderEntity> findAllByFilter(@Param("condition") OrderFilterRequest filterRequest, Pageable pageable);

    @Query(value = "SELECT o FROM OrderEntity o " +
            "LEFT JOIN o.userEntity u " +
            " WHERE " +
            "(:#{#userId} is null or u.id = :#{#userId} )" +
            "AND (:#{#code} is null or lower(o.code) = :#{#code} )" +
            "AND o.deleted=false ORDER BY o.createdDate desc LIMIT 1"
    )
    Optional<OrderEntity> getByCode(Long userId, String code);
}
