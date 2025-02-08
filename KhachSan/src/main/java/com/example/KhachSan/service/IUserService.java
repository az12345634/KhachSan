package com.example.KhachSan.service;
import com.example.KhachSan.entity.UserEntity;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.request.UserRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;

public interface IUserService {
    UserDTO findUserByUsername(String username);
    BaseResponse<?> addUser(UserEntity user);
    BaseResponse<Page<UserDTO>>getAllUsers(UserRequest filterRequest, int page, int size);
    BaseResponse update(Long id ,UserEntity user);
    BaseResponse<?> deleteUser(Long id);
    UserDTO getCurrentUser(Boolean showId);
}
