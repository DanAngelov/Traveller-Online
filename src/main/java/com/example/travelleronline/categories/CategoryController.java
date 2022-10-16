package com.example.travelleronline.categories;

import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-categories")
public class CategoryController extends MasterController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/create")
    public void createCategory(@RequestBody Category c) {
        categoryRepository.save(c);
    }

    @GetMapping()
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable long id) {
        return categoryRepository.findById(id).orElseThrow();//TODO make exception return proper status
    }

    @PutMapping("/{id}")
    public void editCategory(@RequestBody Category c, @PathVariable long id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow();//TODO make exception return proper status
        existingCategory.setName(c.getName());
        categoryRepository.save(existingCategory);
    }

    @DeleteMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public void deleteCategoryById(@PathVariable long id) {
        //TODO Validate data
        categoryRepository.deleteById(id);
    }

    @DeleteMapping(headers = "password=4kd2!kd7@SE1")
    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }


}