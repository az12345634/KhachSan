package com.example.KhachSan.controller.resource;
import com.example.KhachSan.entity.UserEntity;
import com.example.KhachSan.model.dto.UserDTO;
import com.example.KhachSan.model.request.UserRequest;
import com.example.KhachSan.model.respobse.BaseResponse;
import com.example.KhachSan.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/account")
public class UserResource {
    @Autowired
    private IUserService iUserService;
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody UserEntity userEntity) {

        return ResponseEntity.status(HttpStatus.CREATED).body(iUserService.addUser(userEntity));
    }

    @PostMapping("/ListUser")
    public ResponseEntity<BaseResponse<Page<UserDTO>>> ListUser(@RequestBody UserRequest userRequest,
                                                                @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        return ResponseEntity.ok(iUserService.getAllUsers(userRequest,page,size));

    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody UserEntity userEntity) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(iUserService.update(id, userEntity));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<?>> deleteUser(@PathVariable("userId") Long userId) {
        BaseResponse<?> response = iUserService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser() {
        return ResponseEntity.ok(iUserService.getCurrentUser(false));
    }
}
