package com.example.KhachSan.service.impl;

import com.example.KhachSan.entity.CommentEntity;
import com.example.KhachSan.entity.OrderDetailEntity;
import com.example.KhachSan.entity.RoomEntity;
import com.example.KhachSan.model.dto.CommentDTO;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.request.CommentFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.repository.*;
import com.example.KhachSan.service.CommentService;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    // Khai báo các repository cần thiết
    private final CommentRepository commentRepository;
    private final OrderRepository orderRepository;
    private final IUserService iUserService;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Sử dụng ModelMapper để ánh xạ giữa các đối tượng
    @Autowired
    private final ModelMapper modelMapper;

    // Constructor để inject các repository và ModelMapper
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, OrderRepository orderRepository, IUserService iUserService,
                              UserRepository userRepository, RoomRepository roomRepository,
                              OrderDetailRepository orderDetailRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.orderRepository = orderRepository;
        this.iUserService = iUserService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.modelMapper = modelMapper;
    }

    // Phương thức lấy tất cả comment với điều kiện lọc
    @Override
    public BaseResponse<Page<CommentDTO>> getAll(CommentFilterRequest commentFilterRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Lấy dữ liệu từ repository dựa trên điều kiện lọc và phân trang
        Page<CommentEntity> commentEntities = commentRepository.findByFilter(commentFilterRequest, pageable);

        // Ánh xạ các đối tượng CommentEntity sang CommentDTO
        List<CommentDTO> commentDTOS = commentEntities.getContent().stream().map(commentEntity -> {
            CommentDTO commentDTO = modelMapper.map(commentEntity, CommentDTO.class);
            commentDTO.setContent(commentEntity.getContent());
            // Lấy tên người dùng từ UserEntity và thiết lập vào CommentDTO
            commentDTO.setUsername(commentEntity.getUserEntity().getUsername());
            commentDTO.setName(commentEntity.getRoomEntity().getName());
            return commentDTO;
        }).collect(Collectors.toList());

        // Tạo Page<CommentDTO> mới để trả về
        Page<CommentDTO> pageData = new PageImpl(commentDTOS, pageable, commentEntities.getTotalElements());
        BaseResponse<Page<CommentDTO>> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("success");
        response.setData(pageData);
        return response;
    }


    // Phương thức thêm mới comment
    @Override
    public BaseResponse<CommentDTO> addComment(CommentDTO commentDTO) {
        BaseResponse<CommentDTO> response = new BaseResponse<>();
        UserDTO userDTO = iUserService.getCurrentUser(true);

        RoomEntity roomEntity = roomRepository.findByCode(commentDTO.getProductCode());
        commentDTO.setProductId(roomEntity.getId());

        // Kiểm tra xem người dùng đã mua sản phẩm này chưa
        // Kiểm tra xem người dùng đã mua sản phẩm này chưa
        List<OrderDetailEntity> orderDetails = orderDetailRepository.findOrderEntitiesBy(
                userDTO.getId(),
                commentDTO.getProductId()
        );

        if (orderDetails == null || orderDetails.isEmpty()) {  // ✅ Kiểm tra danh sách rỗng
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User has not purchased this product yet");
            return response;
        }

        // Kiểm tra xem người dùng đã comment cho sản phẩm này chưa
        CommentEntity existingComment = commentRepository.findByUserAndProduct(userDTO.getId(), commentDTO.getProductId());

        if (existingComment != null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User has already commented on this product");
            return response;
        }

        // Tạo mới đối tượng CommentEntity và lưu vào cơ sở dữ liệu
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(commentDTO.getContent());

        // Thiết lập người dùng và sản phẩm cho comment
        commentEntity.setUserEntity(userRepository.findById(userDTO.getId()).get());
        commentEntity.setRoomEntity(roomRepository.findById(commentDTO.getProductId()).orElse(null));
        LocalDateTime now = LocalDateTime.now();
        commentEntity.setCreatedDate(now);

        // Lưu comment vào cơ sở dữ liệu
        commentRepository.save(commentEntity);

        try {
            // Chuyển đổi CommentEntity thành CommentDTO để trả về
            CommentDTO savedCommentDTO = modelMapper.map(commentEntity, CommentDTO.class);
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Comment added successfully");
            response.setData(savedCommentDTO);

        } catch (Exception e) {
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Failed to add comment: " + e.getMessage());
        }

        return response;
    }

    @Override
    public BaseResponse<CommentDTO> updateComment(Long commentId, CommentDTO commentDTO) {
        BaseResponse<CommentDTO> response = new BaseResponse<>();
        // Kiểm tra xem comment có tồn tại không
        Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(commentId);
        if (optionalCommentEntity.isEmpty()){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Comment not found");
            return  response;
        }
        //lấy ra commentEntity
        CommentEntity commentEntity = optionalCommentEntity.get();
        //cập nhập nội dung
        commentEntity.setContent(commentDTO.getContent());
        //lưu comment vào data
        try {
            commentEntity = commentRepository.save(commentEntity);
            //chuyển đổi thành commentDTO để trả về
            CommentDTO commentupdate = modelMapper.map(commentEntity, CommentDTO.class);
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Comment update successfully");
            response.setData(commentupdate);
        }catch (Exception e){
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Faile to update comments :" +e.getMessage());
        }

        return response;
    }

    @Override
    public BaseResponse<?> deleteComment(Long commentId) {
        // Log thông báo bắt đầu xóa sản phẩm
        logger.info("Start delete product with id {}", commentId);
        BaseResponse<String> baseResponse = new BaseResponse<>();

        Optional<CommentEntity> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isEmpty()){
            baseResponse.setCode(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage("Comment not found");
            return  baseResponse;
        }
        CommentEntity commentEntity = optionalComment.get();
        commentEntity.setDeleted(true);
        commentRepository.save(commentEntity);

        baseResponse.setCode(HttpStatus.OK.value());
        baseResponse.setMessage("Comment delete successfully");
        return baseResponse;
    }

}
