package com.example.KhachSan.controller.resource;
import com.example.KhachSan.entity.CategoryEntity;
import com.example.KhachSan.model.dto.CategoryDTO;
import com.example.KhachSan.model.request.CategoryFilterRequest;
import com.example.KhachSan.model.response.BaseResponse;
import com.example.KhachSan.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/category")
public class CategoryResource {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<?> addcread(@RequestBody CategoryEntity category){
        return ResponseEntity.ok(categoryService.create(category));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody CategoryEntity categoryEntity) {
        try {
            return ResponseEntity.ok(categoryService.update(id, categoryEntity));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam("id") long id) {

        return ResponseEntity.ok(categoryService.delete(id ));
    }

    @PostMapping()
    public ResponseEntity<BaseResponse<Page<CategoryDTO>>> getAll(@RequestBody CategoryFilterRequest filterRequest,
                                                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size){

        return ResponseEntity.ok(categoryService.getAll(filterRequest,page,size));
    }
}
