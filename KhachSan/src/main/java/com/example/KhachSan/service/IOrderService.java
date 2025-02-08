package com.example.KhachSan.service;

import com.example.KhachSan.model.dto.OrderDTO;
import com.example.KhachSan.model.dto.OrderDetailDTO;
import com.example.KhachSan.model.request.OrderFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;

import java.io.FileNotFoundException;
import java.util.List;


public interface IOrderService {
    BaseResponse<Page<OrderDTO>> getAll(OrderFilterRequest orderFilterRequest, int page, int size);

    BaseResponse<?> createOrder(OrderDTO orderDTO);

    BaseResponse<OrderDTO> getDetailByCode(OrderFilterRequest request);

    BaseResponse<?> createOrderDetail(OrderDetailDTO orderDetailDTO);

    BaseResponse<?> updateOrder(OrderDTO orderDTO);

    BaseResponse<List<OrderDTO>> getOrderByUser();

    BaseResponse<?> getInvoicePdf() throws FileNotFoundException;
}
