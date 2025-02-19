package com.example.KhachSan.service;

import com.example.KhachSan.entity.RoomEntity;
import com.example.KhachSan.model.dto.RoomDTO;
import com.example.KhachSan.model.request.RoomFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {
    BaseResponse<Page<RoomDTO>> getAll(RoomFilterRequest filterRequest, int page, int size);

    BaseResponse<?> updateProduct(String code, RoomDTO roomDTO);

    BaseResponse<?> createProduct(RoomDTO roomDTO);

    BaseResponse<RoomDTO> getProductBy(RoomFilterRequest filterRequest) throws Exception;

    BaseResponse<?> deleteProduct(Long id);
    List<RoomEntity> searchProduct(String keyword); // Thêm phương thức tìm kiếm
    List<RoomEntity> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate);
//BaseResponse<Page<RoomDTO>> findAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, int page, int size);

}
