package com.example.KhachSan.repository;

import com.example.KhachSan.entity.CommentEntity;
import com.example.KhachSan.model.request.CommentFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "SELECT c FROM CommentEntity c " +
            "JOIN c.userEntity userEntity " + // Sử dụng tên biến trong entity
            "JOIN c.roomEntity roomEntity " + // Sử dụng tên biến trong entity
            "WHERE (:#{#filter.userId} IS NULL OR userEntity.id = :#{#filter.userId}) " + // Sử dụng tên biến trong entity
            "AND (:#{#filter.username} IS NULL OR userEntity.username = :#{#filter.username})" +
            "AND (:#{#filter.productId} IS NULL OR roomEntity.id = :#{#filter.productId})" + // Sử dụng tên biến trong entity
            "AND (+:#{#filter.name} IS NULL OR roomEntity.name = :#{#filter.name})" +
            "AND (+:#{#filter.productCode} IS NULL OR roomEntity.code = :#{#filter.productCode})" +
            "AND c.deleted=false ORDER BY c.createdDate desc ")

    Page<CommentEntity> findByFilter(@Param("filter") CommentFilterRequest filter, Pageable pageable);


        @Query("SELECT c FROM CommentEntity c WHERE c.userEntity.id = :userId AND c.roomEntity.id = :productId")
        CommentEntity findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);


    // Thêm một phương thức tìm kiếm sử dụng userId và productId
    @Query(value = "SELECT c FROM CommentEntity c " +
            "JOIN c.userEntity userEntity " + // Sử dụng tên biến trong entity
            "JOIN c.roomEntity productEntity " + // Sử dụng tên biến trong entity
            "WHERE (:userId = :userIdParam OR userEntity.id = :userIdParam) " + // Sử dụng tên biến trong entity
            "AND (:productId = :productIdParam OR productEntity.id = :productIdParam)") // Sử dụng tên biến trong entity
    Page<CommentEntity> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId, Pageable pageable);
}
