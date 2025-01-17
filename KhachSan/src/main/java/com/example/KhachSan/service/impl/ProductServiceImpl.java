package com.example.KhachSan.service.impl;

import com.example.KhachSan.entity.CategoryEntity;
import com.example.KhachSan.entity.ProductEntity;
import com.example.KhachSan.model.dto.ProductDTO;
import com.example.KhachSan.model.request.ProductFilterRequest;
import com.example.KhachSan.model.respobse.BaseResponse;
import com.example.KhachSan.repository.CategoryRepository;
import com.example.KhachSan.repository.ProductRepository;
import com.example.KhachSan.service.IProductService;
import com.example.KhachSan.utils.Constant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public BaseResponse<Page<ProductDTO>> getAll(ProductFilterRequest filterRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productEntities = productRepository.findAllByFilter(filterRequest, pageable);

        List<ProductDTO> productDTOS = productEntities.getContent().stream().map(productEntity -> {
            ProductDTO productDTO = new ProductDTO();
            // Set giá trị productDTO
            try {
                setCommonValueProductDTO(productDTO, productEntity, filterRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return productDTO;
        }).collect(Collectors.toList());

        Page<ProductDTO> pageData = new PageImpl<>(productDTOS, pageable, productEntities.getTotalElements());
        BaseResponse<Page<ProductDTO>> response = new BaseResponse<>();
        response.setCode(200);
        response.setMessage("Get all product successfully");
        response.setData(pageData);
        return response;
    }

    private Boolean checkCategory(ProductDTO productDTO, BaseResponse<?> response) {
        if (getCategory(productDTO.getCategoryId()).isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Category is not exist in system");
            return false;
        }
        return true;
    }



    private Boolean checkProductWithCode(ProductDTO productDTO, BaseResponse<?> response) {
        ProductEntity productExist = productRepository.findByCode(productDTO.getCode());
        if (productExist != null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Mã sản phẩm này đã tồn tại");
            return false;
        }
        return true;
    }

    @Override
    public BaseResponse<?> updateProduct(String code, ProductDTO productDTO) {
        BaseResponse<?> response = new BaseResponse<>();

        ProductEntity productEntity = productRepository.findByCode(code);

        if (productEntity == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Sản phẩm không tồn tại");
            return response;
        }

        if (!checkCategory(productDTO, response)) {
            return response;
        }



        if (!checkProductWithCode(productDTO, response) && !productDTO.getCode().equalsIgnoreCase(code)) {
            return response;
        }
        saveProductEntity(productEntity, productDTO);

        response.setMessage("Cập nhật sản phẩm thành công");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    private Optional<CategoryEntity> getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }


    @Override
    public BaseResponse<?> createProduct(ProductDTO productDTO) {

        BaseResponse<?> response = new BaseResponse<>();

        if (!checkCategory(productDTO, response)) {
            return response;
        }


        if (!checkProductWithCode(productDTO, response)) {
            return response;
        }

        ProductEntity productEntity = new ProductEntity();
        productEntity.setCreatedDate(productDTO.getCreateDate());
        saveProductEntity(productEntity, productDTO);
        response.setMessage("Thêm mới sản phẩm thành công");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    private void saveProductEntity(ProductEntity productEntity, ProductDTO productDTO) {
        productEntity.setName(productDTO.getName());

        if (productDTO.getImage() != null) {
            productEntity.setImage(productDTO.getImage());
        }

        productEntity.setCode(productDTO.getCode());

        productEntity.setPrice(productDTO.getPrice());

        productEntity.setCategoryEntity(getCategory(productDTO.getCategoryId()).get());

        productEntity.setQuantity(productDTO.getQuantity());

        productEntity.setDescription(productDTO.getDescription());

        productEntity.setShortDescription(productDTO.getShortDescription());


        productEntity.setLastModifiedDate(LocalDateTime.now());

        productRepository.save(productEntity);
    }

    @Override
    public BaseResponse<ProductDTO> getProductBy(ProductFilterRequest filterRequest) throws Exception {
        BaseResponse<ProductDTO> response = new BaseResponse<>();
        ProductEntity productEntity = productRepository.findByFilter(filterRequest);
        if (productEntity == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Product with code not exist in system");
            return response;
        }
        ProductDTO productDTO = new ProductDTO();
        // Set giá trị productDTO
        setCommonValueProductDTO(productDTO, productEntity, filterRequest);

        response.setCode(HttpStatus.OK.value());
        response.setMessage("Get product with code successfully");
        response.setData(productDTO);
        return response;
    }

    private void setCommonValueProductDTO(ProductDTO productDTO, ProductEntity productEntity, ProductFilterRequest filterRequest) throws Exception {
        // Set id
        productDTO.setId(productEntity.getId());
        // Set tên
        productDTO.setName(productEntity.getName());
        // Set ảnh
        productDTO.setImage("data:image/jpeg;base64," + Constant.encodeImage(productEntity.getImage()));
        // Set mô tả ngắn
        productDTO.setShortDescription(productEntity.getShortDescription());
        // Set mô tả chi tiết
        productDTO.setDescription(productEntity.getDescription());
        // Set code
        productDTO.setCode(productEntity.getCode());
        // Set số lượng
        productDTO.setQuantity(productEntity.getQuantity());
        // Set giá
        productDTO.setPrice(productEntity.getPrice());
        // Set tên danh mục
        productDTO.setCategory(productEntity.getCategoryEntity().getName());
        // Set id danh mục
        productDTO.setCategoryId(productEntity.getCategoryEntity().getId());


//        // Set giá sản phẩm trong khoảng min - max
//        OptionalDouble minPrice = productEntity.getSizeEntities().stream()
//                .mapToDouble(sizeEntity -> productEntity.getPrice() * sizeEntity.getWeight())
//                .min();
//        productDTO.setMinPrice(minPrice.orElse(0));
//        OptionalDouble maxPrice = productEntity.getSizeEntities().stream()
//                .mapToDouble(sizeEntity -> productEntity.getPrice() * sizeEntity.getWeight())
//                .max();
//        productDTO.setMaxPrice(maxPrice.orElse(0));
//        OptionalDouble price = productEntity.getSizeEntities().stream()
//                .mapToDouble(sizeEntity -> {
//                    double val = 0;
//                    if (sizeEntity.getId().equals(filterRequest.getSizeId())) {
//                        val = productEntity.getPrice() * sizeEntity.getWeight();
//                    }
//                    return val;
//                }).filter(val -> val != 0).findFirst();
//        productDTO.setPriceActive(price.orElse(0));
//        // Size id đang đc chọn
//        productDTO.setSizeActive(filterRequest.getSizeId());
  }

    @Override
    public BaseResponse<?> deleteProduct(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        if (optionalProductEntity.isEmpty()) {
            baseResponse.setCode(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage("error not found");
            return baseResponse;
        }
        ProductEntity productEntity = optionalProductEntity.get();
        productEntity.setDeleted(true);
        productRepository.save(productEntity);

        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setMessage("deleted successfully");
        return baseResponse;
    }

    @Override
    public List<ProductEntity> searchProduct(String keyword) {
        return productRepository.searchBySearch(keyword);

    }
}