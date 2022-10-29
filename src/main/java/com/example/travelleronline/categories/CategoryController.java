package com.example.travelleronline.categories;

import com.example.travelleronline.categories.dtos.CategoryDTO;
import com.example.travelleronline.general.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController extends MasterController {

    public static final String ADMIN_PASSWORD = "4kd2!kd7@SE1";
    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "/categories", headers = "password=" + ADMIN_PASSWORD)
    public CategoryDTO createCategory(@RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping(value = "/categories/{cid}", headers = "password=" + ADMIN_PASSWORD)
    public CategoryDTO editCategory(@RequestBody CategoryDTO dto,@PathVariable int cid) {
        return categoryService.editCategory(dto, cid);
    }

    @DeleteMapping(value = "/categories/{cid}", headers = "password=" + ADMIN_PASSWORD)
    public void deleteCategoryById(@PathVariable int cid) {
        categoryService.deleteCategoryById(cid);
    }

}