package com.example.KhachSan.repository;

import com.example.KhachSan.entity.RoomEntity;
import com.example.KhachSan.model.request.RoomFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query(value = "SELECT DISTINCT p FROM RoomEntity p " +
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
    Page<RoomEntity> findAllByFilter(@Param("condition") RoomFilterRequest filterRequest, Pageable pageable);

    @Query(value = "SELECT p FROM RoomEntity p " +
            "LEFT JOIN p.categoryEntity c " +
            " WHERE " +
            " (:#{#condition.code} is null or lower(p.code) = :#{#condition.code}) " +
            "AND p.deleted=false ORDER BY p.createdDate desc LIMIT 1"
    )
    RoomEntity findByFilter(@Param("condition") RoomFilterRequest filterRequest);

    @Query(value = "SELECT p FROM RoomEntity p " +
            "LEFT JOIN p.categoryEntity c " +
            " WHERE " +
            " (:#{#code} is null or lower(p.code) = :#{#code}) " +
            "AND p.deleted=false ORDER BY p.createdDate desc LIMIT 1"
    )
    RoomEntity findByCode(String code);

    @Query("SELECT r FROM RoomEntity r WHERE r.deleted = false AND (" +
            "  SELECT COUNT(od) FROM OrderDetailEntity od " +
            "  JOIN od.orderEntity o " +
            "  WHERE od.roomEntity.id = r.id " +
            "  AND o.status = 2 " +
            "  AND (:checkInDate < o.checkOutDate AND :checkOutDate > o.checkInDate)" +
            ") < r.quantity")
    List<RoomEntity> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate,
                                        @Param("checkOutDate") LocalDate checkOutDate);

//    @Query("SELECT r FROM RoomEntity r WHERE r.deleted = false AND (" +
//            "  SELECT COUNT(od) FROM OrderDetailEntity od " +
//            "  JOIN od.orderEntity o " +
//            "  WHERE od.roomEntity.id = r.id " +
//            "  AND o.status = 2 " +
//            "  AND (:checkInDate < o.checkOutDate AND :checkOutDate > o.checkInDate)" +
//            ") < r.quantity")
//    Page<RoomEntity> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate,
//                                        @Param("checkOutDate") LocalDate checkOutDate,
//                                        Pageable pageable);


    @Query("SELECT nv FROM RoomEntity nv WHERE nv.name LIKE %:keyword% OR nv.code LIKE %:keyword%")
    List<RoomEntity> searchBySearch(@Param("keyword") String keyword);
}