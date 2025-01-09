package com.example.KhachSan.service.impl;
import com.example.KhachSan.entity.RoleEntity;
import com.example.KhachSan.entity.UserEntity;
import com.example.KhachSan.model.dto.RoleDTO;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.request.UserRequest;
import com.example.KhachSan.model.respobse.BaseResponse;
import com.example.KhachSan.repository.RoleRepository;
import com.example.KhachSan.repository.UserRepository;
import com.example.KhachSan.service.IUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        logger.info("Tạo ra bean: {}", UserServiceImpl.class);
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        UserDTO userDto = modelMapper.map(userEntity, UserDTO.class);
        List<RoleDTO> roleDtos = roleRepository.getRoleByUsername(username).stream().map(role -> modelMapper.map(role, RoleDTO.class)).toList();
        userDto.setRoleDTOS(roleDtos);
        return userDto;
    }
    @Override
    public BaseResponse<?> addUser(UserEntity user) {
        BaseResponse<?> baseResponse = new BaseResponse<>();

        // Kiểm tra xem tài khoản đã tồn tại trong cơ sở dữ liệu chưa
        if (userRepository.findByUsername(user.getUsername()) != null) {
            // Thông báo rằng tên người dùng đã tồn tại trong hệ thống
            baseResponse.setCode(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Tài khoản đã tồn tại trong hệ thống");
            return baseResponse;
        }

        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán mặc định vai trò cho người dùng (ví dụ: ROLE_USER)
        // Bạn có thể tùy chỉnh logic gán vai trò ở đây
        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");
        user.setRoles(Collections.singleton(userRole));

        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setMessage("Đăng ký thành công");
        userRepository.save(user);
        return baseResponse;
    }

    /// hiển thị thông tin admin
    @Override
    public BaseResponse<Page<UserDTO>> getAllUsers(UserRequest userRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userEntities = userRepository.findFirstByUserId(userRequest, pageable);

        List<UserDTO> userEntityList = userEntities.getContent().stream().map(userEntity -> {
            UserDTO userDTO = modelMapper.map(userEntity, UserDTO.class);
            userDTO.setId(userEntity.getId());
            userDTO.setFullName(userEntity.getFullName());
            userDTO.setUsername(userEntity.getUsername());
            userDTO.setEmail(userEntity.getEmail());
            userDTO.setPhone(userEntity.getPhone());
            return userDTO;
        }).collect(Collectors.toList());

        Page<UserDTO> pageData = new PageImpl<>(userEntityList, pageable, userEntities.getTotalElements());
        BaseResponse<Page<UserDTO>> baseResponseresponse = new BaseResponse<>();
        baseResponseresponse.setCode(HttpStatus.OK.value());
        baseResponseresponse.setMessage("Thành Công");
        baseResponseresponse.setData(pageData);
        return baseResponseresponse;
    }

    @Override
    public BaseResponse<?> deleteUser(Long userId) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()) {
            baseResponse.setCode(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage("not user");
            return baseResponse;
        }
        UserEntity user = optionalUserEntity.get();
        user.setDeleted(true);
        userRepository.save(user);

        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setMessage("user delete successfully");
        return baseResponse;
    }


    @Override
    public BaseResponse<?> update(Long id ,UserEntity user) {
        BaseResponse<?> baseResponse = new BaseResponse<>();
        Optional<UserEntity> userEntity = userRepository.findById(id);
        userEntity.get().setUsername(user.getUsername());
        userEntity.get().setFullName(user.getFullName());
        userEntity.get().setEmail(user.getEmail());
        userEntity.get().setPhone(user.getPhone());
        userRepository.save(userEntity.get());
        return baseResponse;
    }

    @Override
    public UserDTO getCurrentUser(Boolean showId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                // Lấy thông tin người dùng từ Principal
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Lấy giá trị username đăng nhập
                String username = userDetails.getUsername();
                // Lấy dữ liệu bản ghi user đang đăng nhập
                userEntity = userRepository.findByUsername(username);
                // Set giá trị userId cho filter
            }
        }
        UserDTO userDTO = new UserDTO();
        if (userEntity != null) {
            userDTO.setUsername(userEntity.getUsername());
            if(showId){
                userDTO.setId(userEntity.getId());
            }
        }

        return userDTO;
    }


}
