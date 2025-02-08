package com.example.KhachSan.controller.resource;
import com.example.KhachSan.model.dto.OrderDTO;
import com.example.KhachSan.model.dto.OrderDetailDTO;
import com.example.KhachSan.model.request.OrderFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.service.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("api/order")
public class OrderResource {

    private final IOrderService service;

    public OrderResource(IOrderService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<Page<OrderDTO>>> getAll(@RequestBody OrderFilterRequest filterRequest,
                                                               @RequestParam(name = "page",required = false,defaultValue = "0") int page,
                                                               @RequestParam(name = "size",required = false,defaultValue = "10") int size){

        return ResponseEntity.ok(service.getAll(filterRequest,page,size));
    }
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(service.createOrder(orderDTO));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(service.updateOrder(orderDTO));
    }
    @PostMapping("/detail/create")
    public ResponseEntity<?> createOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(service.createOrderDetail(orderDetailDTO));
    }
    @PostMapping("/detail")
    public ResponseEntity<?> getByCode(@RequestBody OrderFilterRequest request) {
        return ResponseEntity.ok(service.getDetailByCode(request));
    }

    @GetMapping("/invoice")
    public ResponseEntity<?> getInvoicePdf() throws FileNotFoundException {
        return ResponseEntity.ok(service.getInvoicePdf());
    }
}
