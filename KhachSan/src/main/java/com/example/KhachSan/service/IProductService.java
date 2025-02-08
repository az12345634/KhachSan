package com.example.KhachSan.service;

import com.example.KhachSan.entity.ProductEntity;
import com.example.KhachSan.model.dto.ProductDTO;
import com.example.KhachSan.model.request.ProductFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    BaseResponse<Page<ProductDTO>> getAll(ProductFilterRequest filterRequest, int page, int size);

    BaseResponse<?> updateProduct(String code,ProductDTO productDTO);

    BaseResponse<?> createProduct(ProductDTO productDTO);

    BaseResponse<ProductDTO> getProductBy(ProductFilterRequest filterRequest) throws Exception;

    BaseResponse<?> deleteProduct(Long id);
    List<ProductEntity> searchProduct(String keyword); // Thêm phương thức tìm kiếm
}
