package com.example.KhachSan.service.impl;

import com.example.KhachSan.entity.*;
import com.example.KhachSan.model.dto.CartDTO;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.repository.CartRepository;
import com.example.KhachSan.repository.ProductRepository;
import com.example.KhachSan.repository.UserRepository;
import com.example.KhachSan.service.ICartService;
import com.example.KhachSan.service.IUserService;
import com.example.KhachSan.utils.Constant;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CartServiceImpl implements ICartService {
    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IUserService userService;

    @Override
    public BaseResponse<Page<CartDTO>> getAll(int page, int size) {
        BaseResponse<Page<CartDTO>> response = new BaseResponse<>();
        UserDTO userDTO = userService.getCurrentUser(true);
        // Lấy ra userId đang đăng nhập
        Long userId = userDTO.getId();
        if (userId == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Người dùng chưa đăng nhập hoặc không tồn tại trong hệ thống");
            return response;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CartEntity> cartEntities = cartRepository.findAllByFilter(userId, pageable);

        List<CartDTO> cartDTOS = cartEntities.getContent().stream().map(cartEntity -> {
            CartDTO cartDTO = new CartDTO();
            // Set id
            cartDTO.setId(cartEntity.getId());


            // Set username thêm sản phẩm vào giỏ hàng
            cartDTO.setCreatedBy(cartEntity.getUserEntity().getUsername());

            // Set id sản phẩm
            cartDTO.setProductId(cartEntity.getProductEntity().getId());

            // Set tên sản phẩm
            String productName = cartEntity.getProductEntity().getName();
            cartDTO.setProductName(productName);

            cartDTO.setProductCode(cartEntity.getProductEntity().getCode());
            // Set giá sản phẩm
            Double productPrice = cartEntity.getProductEntity().getPrice();
            cartDTO.setProductPrice(productPrice);

            // Set số lượng sản phẩm
            Integer quantity = cartEntity.getQuantity();
            cartDTO.setQuantity(quantity);

            // Set tổng tiền trên 1 sản phẩm
            Double totalOneP = productPrice  * quantity;
            cartDTO.setTotalOneP(totalOneP);
            try {
                cartDTO.setImgProduct("data:image/jpeg;base64," + Constant.encodeImage(cartEntity.getProductEntity().getImage()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return cartDTO;
        }).collect(Collectors.toList());

        Page<CartDTO> pageData = new PageImpl<>(cartDTOS, pageable, cartEntities.getTotalElements());

        response.setCode(200);
        response.setMessage("Lấy tất cả sản phẩm trong giỏ hàng thành công");
        response.setData(pageData);
        return response;
    }

    @Override
    public BaseResponse<?> addToCart(CartDTO cartDTO) {
        BaseResponse<?> response = new BaseResponse<>();
        UserDTO userDTO = userService.getCurrentUser(true);
        // Lấy ra userId đang đăng nhập
        Long userId = userDTO.getId();
        if (userId == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Người dùng không đăng nhập hoặc không tồn tại trong hệ thống");
            return response;
        }

        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        Optional<ProductEntity> product = productRepository.findById(cartDTO.getProductId());

        if (product.isEmpty()) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Sản phẩm không tồn tại trong hệ thống");
            return response;
        }


        Optional<CartEntity> cartExist = cartRepository.cartExist(userId, product.get().getId());
        cartExist.ifPresent(dto -> cartDTO.setId(dto.getId()));

        CartEntity cartEntity = new CartEntity();
        cartEntity.setId(cartDTO.getId());
        cartEntity.setUserEntity(userEntity);
        cartEntity.setProductEntity(product.get());
        cartEntity.setTotal(cartDTO.getTotalOneP());
        cartEntity.setQuantity(cartDTO.getQuantity());
        LocalDateTime now = LocalDateTime.now();
        cartEntity.setCreatedDate(now);
        cartEntity.setCreatedBy(userDTO.getUsername());
        cartRepository.save(cartEntity);
        response.setMessage("Thêm vào giỏ hàng thành công");
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    public BaseResponse<Long> countCart() {
        BaseResponse<Long> response = new BaseResponse<>();
        UserDTO userDTO = userService.getCurrentUser(true);
        Long userId = userDTO.getId();
        if (userId == null) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("User is not login or not exit in system");
            return response;
        }
        Long countProductCart = cartRepository.countCartByUser(userId);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Get count product cart successfully");
        response.setData(countProductCart);
        return response;
    }

    public BaseResponse<?> deleteCart(CartDTO cartDTO) {
        BaseResponse<?> response = new BaseResponse<>();
        CartEntity cartEntity = modelMapper.map(cartDTO, CartEntity.class);
        cartRepository.delete(cartEntity);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Delete cart successfully");
        return response;
    }
}
