package com.example.KhachSan.service.impl;


import com.example.KhachSan.model.respobse.BaseResponse;
import com.example.KhachSan.service.IFileService;
import com.example.KhachSan.utils.Constant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Service
public class FileServiceImpl implements IFileService {
    @Override
    public BaseResponse<String> storageImage(MultipartFile file) throws Exception {
        BaseResponse<String> response = new BaseResponse<>();
        String filePath = saveFileImageReturnPath(file);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Upload image successfully");
        response.setData(filePath);
        return response;
    }
    private String saveFileImageReturnPath(MultipartFile file) throws IOException {
        File directory = new File(Constant.IMAGE_SAVE_PATH);
        if (!directory.exists()) { //Kiểm tra thư mục đã tồn tại chưa
            directory.mkdirs();//Tạo các thư mục theo path cần thiết
        }
        long timestamp = Instant.now().getEpochSecond(); //Lấy thời gian hiện tại trả về timestamp

        int lastIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf('.');
        String extension = file.getOriginalFilename().substring(lastIndex + 1);
        String fileName = timestamp + "." + extension;

        String filePathSave = Constant.IMAGE_SAVE_PATH + fileName; //Đường dẫn lưu ảnh

        file.transferTo(new File(filePathSave)); //Lưu trữ ảnh vào trong folder

        return filePathSave;
    }
}
