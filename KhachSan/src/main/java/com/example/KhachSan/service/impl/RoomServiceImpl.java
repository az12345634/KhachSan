package com.example.KhachSan.service.impl;

import com.example.KhachSan.entity.CategoryEntity;
import com.example.KhachSan.entity.RoomEntity;
import com.example.KhachSan.model.dto.RoomDTO;
import com.example.KhachSan.model.request.RoomFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.repository.CategoryRepository;
import com.example.KhachSan.repository.RoomRepository;
import com.example.KhachSan.service.IRoomService;
import com.example.KhachSan.utils.Constant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements IRoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public BaseResponse<Page<RoomDTO>> getAll(RoomFilterRequest filterRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomEntity> productEntities = roomRepository.findAllByFilter(filterRequest, pageable);

        List<RoomDTO> roomDTOS = productEntities.getContent().stream().map(productEntity -> {
            RoomDTO roomDTO = new RoomDTO();
            // Set giá trị productDTO
            try {
                setCommonValueProductDTO(roomDTO, productEntity, filterRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return roomDTO;
        }).collect(Collectors.toList());

        Page<RoomDTO> pageData = new PageImpl<>(roomDTOS, pageable, productEntities.getTotalElements());
        BaseResponse<Page<RoomDTO>> response = new BaseResponse<>();
        response.setCode(200);
        response.setMessage("Get all product successfully");
        response.setData(pageData);
        return response;
    }

    private Boolean checkCategory(RoomDTO roomDTO, BaseResponse<?> response) {
        if (getCategory(roomDTO.getCategoryId()).isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Category is not exist in system");
            return false;
        }
        return true;
    }



    private Boolean checkProductWithCode(RoomDTO roomDTO, BaseResponse<?> response) {
        RoomEntity productExist = roomRepository.findByCode(roomDTO.getCode());
        if (productExist != null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Mã sản phẩm này đã tồn tại");
            return false;
        }
        return true;
    }

    @Override
    public BaseResponse<?> updateProduct(String code, RoomDTO roomDTO) {
        BaseResponse<?> response = new BaseResponse<>();

        RoomEntity roomEntity = roomRepository.findByCode(code);

        if (roomEntity == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Sản phẩm không tồn tại");
            return response;
        }

        if (!checkCategory(roomDTO, response)) {
            return response;
        }



        if (!checkProductWithCode(roomDTO, response) && !roomDTO.getCode().equalsIgnoreCase(code)) {
            return response;
        }
        saveProductEntity(roomEntity, roomDTO);

        response.setMessage("Cập nhật sản phẩm thành công");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    private Optional<CategoryEntity> getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }


    @Override
    public BaseResponse<?> createProduct(RoomDTO roomDTO) {

        BaseResponse<?> response = new BaseResponse<>();

        if (!checkCategory(roomDTO, response)) {
            return response;
        }


        if (!checkProductWithCode(roomDTO, response)) {
            return response;
        }

        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setCreatedDate(roomDTO.getCreateDate());
        saveProductEntity(roomEntity, roomDTO);
        response.setMessage("Thêm mới sản phẩm thành công");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    private void saveProductEntity(RoomEntity roomEntity, RoomDTO roomDTO) {
        roomEntity.setName(roomDTO.getName());

        if (roomDTO.getImage() != null) {
            roomEntity.setImage(roomDTO.getImage());
        }

        roomEntity.setCode(roomDTO.getCode());

        roomEntity.setPrice(roomDTO.getPrice());

        roomEntity.setCategoryEntity(getCategory(roomDTO.getCategoryId()).get());

        roomEntity.setQuantity(roomDTO.getQuantity());

        roomEntity.setDescription(roomDTO.getDescription());

        roomEntity.setShortDescription(roomDTO.getShortDescription());
        roomEntity.setAddress(roomDTO.getAddress());

        roomEntity.setLastModifiedDate(LocalDateTime.now());

        roomRepository.save(roomEntity);
    }

    @Override
    public BaseResponse<RoomDTO> getProductBy(RoomFilterRequest filterRequest) throws Exception {
        BaseResponse<RoomDTO> response = new BaseResponse<>();
        RoomEntity roomEntity = roomRepository.findByFilter(filterRequest);
        if (roomEntity == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Product with code not exist in system");
            return response;
        }
        RoomDTO roomDTO = new RoomDTO();
        // Set giá trị productDTO
        setCommonValueProductDTO(roomDTO, roomEntity, filterRequest);

        response.setCode(HttpStatus.OK.value());
        response.setMessage("Get product with code successfully");
        response.setData(roomDTO);
        return response;
    }

    private void setCommonValueProductDTO(RoomDTO roomDTO, RoomEntity roomEntity, RoomFilterRequest filterRequest) throws Exception {
        // Set id
        roomDTO.setId(roomEntity.getId());
        // Set tên
        roomDTO.setName(roomEntity.getName());

        // Set mô tả ngắn
        roomDTO.setShortDescription(roomEntity.getShortDescription());
        // Set mô tả chi tiết
        roomDTO.setDescription(roomEntity.getDescription());
        // Set code
        roomDTO.setCode(roomEntity.getCode());
        // Set số lượng
        roomDTO.setQuantity(roomEntity.getQuantity());

        roomDTO.setAddress(roomEntity.getAddress());
        // Set giá
        roomDTO.setPrice(roomEntity.getPrice());
        // Set tên danh mục
        roomDTO.setCategory(roomEntity.getCategoryEntity().getName());
        // Set id danh mục
        roomDTO.setCategoryId(roomEntity.getCategoryEntity().getId());
        // Set ảnh
        roomDTO.setImage("data:image/jpeg;base64," + Constant.encodeImage(roomEntity.getImage()));


  }

    @Override
    public BaseResponse<?> deleteProduct(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        Optional<RoomEntity> optionalProductEntity = roomRepository.findById(id);
        if (optionalProductEntity.isEmpty()) {
            baseResponse.setCode(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage("error not found");
            return baseResponse;
        }
        RoomEntity roomEntity = optionalProductEntity.get();
        roomEntity.setDeleted(true);
        roomRepository.save(roomEntity);

        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setMessage("deleted successfully");
        return baseResponse;
    }

    @Override
    public List<RoomEntity> searchProduct(String keyword) {
        return roomRepository.searchBySearch(keyword);

    }

//    @Override
//    public List<RoomEntity> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
//        if (checkInDate == null || checkOutDate == null || !checkInDate.isBefore(checkOutDate)) {
//            throw new IllegalArgumentException("Ngày check-in và check-out không hợp lệ.");
//        }
//
//        return roomRepository.findAvailableRooms(checkInDate, checkOutDate);
//    }




    @Override
    public List<RoomEntity> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates must not be null.");
        }
        if (checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before or equal to check-out date.");
        }

        List<RoomEntity> availableRooms = roomRepository.findAvailableRooms(checkInDate, checkOutDate);

        // Loại bỏ các phòng bị trùng lặp (nếu vẫn còn xảy ra vấn đề)
        return availableRooms.stream().distinct().collect(Collectors.toList());
    }
//@Override
//public BaseResponse<Page<RoomDTO>> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, int page, int size) {
//    if (checkInDate == null || checkOutDate == null) {
//        throw new IllegalArgumentException("Check-in and check-out dates must not be null.");
//    }
//    if (checkInDate.isAfter(checkOutDate)) {
//        throw new IllegalArgumentException("Check-in date must be before or equal to check-out date.");
//    }
//
//    Pageable pageable = PageRequest.of(page, size);
//    Page<RoomEntity> roomEntities = roomRepository.findAvailableRooms(checkInDate, checkOutDate, pageable);
//
//    List<RoomDTO> roomDTOS = roomEntities.getContent().stream().map(roomEntity -> {
//        RoomDTO roomDTO = new RoomDTO();
//        try {
//            setCommonValueProductDTO(roomDTO, roomEntity, null);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return roomDTO;
//    }).collect(Collectors.toList());
//
//    Page<RoomDTO> pageData = new PageImpl<>(roomDTOS, pageable, roomEntities.getTotalElements());
//    BaseResponse<Page<RoomDTO>> response = new BaseResponse<>();
//    response.setCode(200);
//    response.setMessage("Get available rooms successfully");
//    response.setData(pageData);
//    return response;
//}
//

}

