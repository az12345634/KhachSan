package com.example.KhachSan.repository;

import com.example.KhachSan.entity.*;
import com.example.KhachSan.model.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    @Query(value = "SELECT e FROM UserEntity e " +
            "LEFT JOIN e.roles c " +
            "WHERE (:#{#condition.userAId} IS NULL OR c.id = :#{#condition.userAId}) " +
            "AND e.deleted = false ORDER BY e.createdDate",
            countQuery = "SELECT count(e) FROM UserEntity e " +
                    "LEFT JOIN e.roles c " +
                    "WHERE (:#{#condition.userAId} IS NULL OR c.id = :#{#condition.userAId}) " +
                    "AND e.deleted = false")
    Page<UserEntity >findFirstByUserId(@Param("condition") UserRequest filterRequest, Pageable pageable);
}