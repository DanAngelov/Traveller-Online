package com.example.travelleronline.categories;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryService extends MasterService {

    public CategoryDTO createCategory(CategoryDTO dto){
        validateCategoryName(dto.getName());
        Category c = new Category();
        c.setName(dto.getName());
        categoryRepository.save(c);
        return dto;
    }

    private CategoryDTO mapCategoryToCategoryDTO(Category c) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(c.getCategoryId());
        dto.setName(c.getName());
        List<Integer> postIds = new ArrayList<>();
        for (Post p : c.getPosts()) {
            postIds.add(p.getPostId());
        }
        dto.setPostIds(postIds);
        return dto;
    }

    public List<CategoryDTO> getAllCategories(){
        List<CategoryDTO> dtos = new ArrayList<>();
        for (Category c : categoryRepository.findAll()){
            CategoryDTO dto = mapCategoryToCategoryDTO(c);
            dtos.add(dto);
        }
        return dtos;
    }

    public CategoryDTO getCategoryById(int id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        return mapCategoryToCategoryDTO(c);
    }

    public CategoryDTO editCategory(CategoryDTO dto, int id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        validateCategoryName(dto.getName());
        existingCategory.setName(dto.getName());
        categoryRepository.save(existingCategory);
        return dto;
    }

    public void deleteCategoryById(int id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        // Soft delete
        c.setName("Deleted category" + System.nanoTime());
        categoryRepository.save(c);
    }

    private void validateCategoryName(String category){
        if(category == null || category.equals("null")) {
            throw new BadRequestException("Category can not be null.");
        }
        if(category.length() < 3 || category.isBlank() || category.length() > 10) {
            throw new BadRequestException("Category name must be between 3 and 100 letters");
        }
        List<Category> categories = categoryRepository.findAll();
        for (Category c : categories) {
            if(category.equals(c.getName())) {
                throw new BadRequestException("Category name already exists.");
            }
        }
    }

}
