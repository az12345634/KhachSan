package com.example.KhachSan.repository;

import com.example.KhachSan.entity.CartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    @Query(value = "SELECT c FROM CartEntity c " +
            "LEFT JOIN c.userEntity u " +
            "LEFT JOIN c.roomEntity p " +
            " WHERE " +
            " (:#{#userId} is null or u.id = :#{#userId}) " +
            "AND c.deleted=false ORDER BY p.createdDate desc "
    )
    Page<CartEntity> findAllByFilter(Long userId, Pageable pageable);

    @Query(value = "SELECT COUNT(c) FROM CartEntity c" +
            " WHERE " +
            " (:#{#userId} is null or c.userEntity.id = :#{#userId}) AND c.deleted = false"
    )
    Long countCartByUser(Long userId);

    @Query(value = "SELECT c FROM CartEntity c " +
            "LEFT JOIN c.userEntity u " +
            "LEFT JOIN c.roomEntity p " +
            " WHERE " +
            " (:#{#userId} is null or u.id = :#{#userId}) " +
            "AND (:#{#productId} is null or p.id = :#{#productId}) " +
            "AND c.deleted = false ORDER BY c.createdDate desc LIMIT 1"
    )
    Optional<CartEntity> cartExist(Long userId, Long productId);

    @Query(value = "select c from CartEntity c where c.id in :ids")
    Set<CartEntity> findByIds(List<Long> ids);
}
