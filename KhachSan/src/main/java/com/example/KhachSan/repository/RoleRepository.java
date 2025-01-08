package com.example.KhachSan.repository;


import com.example.KhachSan.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {

    @Query(value = "select r from RoleEntity r join r.users u where u.username=:username")
    List<RoleEntity> getRoleByUsername(@Param("username") String username);

}