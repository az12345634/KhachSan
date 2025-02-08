package com.example.KhachSan.service;

import com.example.KhachSan.entity.CategoryEntity;
import com.example.KhachSan.model.dto.CategoryDTO;
import com.example.KhachSan.model.request.CategoryFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    BaseResponse<Page<CategoryDTO>> getAll(CategoryFilterRequest filterRequest, int page, int size);

    CategoryEntity create(CategoryEntity category);
    CategoryEntity findById(Long id);
    BaseResponse update(Long id , CategoryEntity category);

    BaseResponse<?> delete(Long id);
}
