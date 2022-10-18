package com.example.travelleronline.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping(headers = "password=4kd2!kd7@SE1")
    public CategoryDTO createCategory(@RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @GetMapping()
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategoryById(@PathVariable long id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public CategoryDTO editCategory(@RequestBody CategoryDTO dto, @PathVariable long id) {
        return categoryService.editCategory(dto, id);
    }

    @DeleteMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public void deleteCategoryById(@PathVariable long id) {
        categoryService.deleteCategoryById(id);
    }

    @DeleteMapping(headers = "password=4kd2!kd7@SE1")
    public void deleteAllCategories() {
        categoryService.deleteAllCategories();
    }


}