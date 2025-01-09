package com.example.KhachSan.model.request;

import lombok.Data;
@Data
public class UserRequest {
    private Long userAId;
    private String fullName;
    private String username;
    private String password;
    private String phone;
}