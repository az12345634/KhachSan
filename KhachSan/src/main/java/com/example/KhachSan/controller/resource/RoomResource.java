package com.example.KhachSan.controller.resource;

import com.example.KhachSan.entity.RoomEntity;
import com.example.KhachSan.model.dto.RoomDTO;
import com.example.KhachSan.model.request.RoomFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.service.IRoomService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/product")
public class RoomResource {

    private final IRoomService service;
    @GetMapping("/search")
    public List<RoomEntity> searchSinhVien(@RequestParam("keyword") String keyword) {
        return service.searchProduct(keyword);
    }
    @GetMapping("/available")
    public ResponseEntity<List<RoomEntity>> findAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        List<RoomEntity> availableRooms = service.findAvailableRooms(checkInDate, checkOutDate);
        if (availableRooms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(availableRooms);
        }
        return ResponseEntity.ok(availableRooms);
    }

//@GetMapping("/rooms/available")
//public ResponseEntity<?> getAvailableRooms(@RequestParam("checkInDate") String checkInDateStr,
//                                           @RequestParam("checkOutDate") String checkOutDateStr) {
//    try {
//        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
//        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);
//        List<RoomEntity> availableRooms = service.findAvailableRooms(checkInDate, checkOutDate);
//        return ResponseEntity.ok(availableRooms);
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ngày không hợp lệ hoặc không còn phòng trống..");
//    }
//}


    public RoomResource(IRoomService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<Page<RoomDTO>>> getAll(@RequestBody RoomFilterRequest filterRequest,
                                                              @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAll(filterRequest, page, size));
    }

    @PostMapping("/create")
    public ResponseEntity<?> updateProduction(@RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(service.createProduct(roomDTO));
    }

    @PostMapping("/update/{code}")
    public ResponseEntity<?> updateProduction(@PathVariable("code") String code, @RequestBody RoomDTO roomDTO) throws Exception {
        return ResponseEntity.ok(service.updateProduct(code, roomDTO));
    }

    @PostMapping("/detail")
    public ResponseEntity<?> getProductBy(@RequestBody RoomFilterRequest filterRequest) throws Exception {
        return ResponseEntity.ok(service.getProductBy(filterRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deleteProduct(@PathVariable("id") Long id) {
        BaseResponse<?> response = service.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}