package com.example.travelleronline.categories;

import com.example.travelleronline.categories.dtos.CategoryDTO;
import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.MasterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryService extends MasterService {

    private static final int CATEGORY_NAME_LENGTH_MIN = 3;
    private static final int CATEGORY_NAME_LENGTH_MAX = 30;

    CategoryDTO createCategory(CategoryDTO dto){
        validateCategoryName(dto.getName());
        Category c = new Category();
        c.setName(dto.getName());
        categoryRepository.save(c);
        return dto;
    }


    List<CategoryDTO> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(c -> modelMapper.map(c,CategoryDTO.class))
                .collect(Collectors.toList());
    }

    CategoryDTO editCategory(CategoryDTO dto, int cid) {
        Category existingCategory = getCategoryById(cid);
        validateCategoryName(dto.getName());
        existingCategory.setName(dto.getName());
        categoryRepository.save(existingCategory);
        return dto;
    }

    void deleteCategoryById(int cid) {
        Category c = getCategoryById(cid);
        // Soft delete
        c.setName("Deleted category" + System.nanoTime());
        categoryRepository.save(c);
    }

    private void validateCategoryName(String category){
        if(category == null || category.isBlank()) {
            throw new BadRequestException("Category can not be blank.");
        }
        if(category.length() < CATEGORY_NAME_LENGTH_MIN || category.length() > CATEGORY_NAME_LENGTH_MAX) {
            throw new BadRequestException("Category name must be between " +
                    CATEGORY_NAME_LENGTH_MIN + " and " + CATEGORY_NAME_LENGTH_MAX + " letters.");
        }
        Category c = categoryRepository.findByName(category);
        if (c != null) {
            throw new BadRequestException("Category with this name already exists.");
        }
    }

}