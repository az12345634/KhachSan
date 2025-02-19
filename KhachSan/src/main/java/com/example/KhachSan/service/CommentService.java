package com.example.KhachSan.service;

import com.example.KhachSan.model.dto.CommentDTO;
import com.example.KhachSan.model.request.CommentFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    BaseResponse<Page<CommentDTO>> getAll(CommentFilterRequest commentFilterRequest, int page, int size);
    BaseResponse<CommentDTO> addComment(CommentDTO commentDTO);
    BaseResponse<CommentDTO> updateComment(Long commentId, CommentDTO commentDTO);

    BaseResponse<?> deleteComment(Long commentId);
}
