package com.example.KhachSan.model.dto;

import lombok.Data;

import java.util.List;
@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phone;
    List<RoleDTO> roleDTOS;


}