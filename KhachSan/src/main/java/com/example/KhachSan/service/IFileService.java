package com.example.KhachSan.service;


import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    BaseResponse<String> storageImage(MultipartFile file) throws Exception;
}
