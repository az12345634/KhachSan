package com.example.KhachSan.model.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime modifiedDate;
    private String modifiedBy;
    private Long productId;
    private Long userId;
    private String username;
    private String name;
    private String productCode;
}
