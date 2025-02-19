package com.example.KhachSan.controller.resource;

import com.example.KhachSan.model.dto.CommentDTO;
import com.example.KhachSan.model.request.CommentFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
public class CommentResource {
    private final CommentService commentService;

    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<Page<CommentDTO>>> getAll(@RequestBody CommentFilterRequest filterRequest,
                                                                 @RequestParam(name = "page",required = false,defaultValue = "0") int page,
                                                                 @RequestParam(name = "size",required = false,defaultValue = "10") int size){

        return ResponseEntity.ok(commentService.getAll(filterRequest,page,size));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<CommentDTO>> addComment(@RequestBody CommentDTO commentDTO) {
        if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getProductCode() == null) {
            // Trả về lỗi nếu thông tin comment không hợp lệ
            BaseResponse<CommentDTO> response = new BaseResponse<>();
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid comment data");
            return ResponseEntity.badRequest().body(response);
        }

        // Thực hiện kiểm tra và xử lý comment ở đây
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(commentDTO));
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<BaseResponse<CommentDTO>> updateComment(@PathVariable Long commentId,
                                                                  @RequestBody CommentDTO commentDTO) {
        BaseResponse<CommentDTO> response = commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<?>> deletecommet(@PathVariable("id") Long commentId){
        BaseResponse<?> baseResponse = commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }
}
