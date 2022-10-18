package com.example.travelleronline.categories;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    public CategoryDTO createCategory(CategoryDTO dto){
        validateCategoryName(dto.getName());
        Category c = new Category();
        c.setName(dto.getName());
        categoryRepository.save(c);
        return modelMapper.map(c, CategoryDTO.class);
    }

    public List<CategoryDTO> getAllCategories(){
        return  categoryRepository.findAll().stream().map(c -> modelMapper.map(c,CategoryDTO.class)).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(long id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        return modelMapper.map(c, CategoryDTO.class);
    }

    public CategoryDTO editCategory(CategoryDTO dto, long id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        validateCategoryName(dto.getName());
        existingCategory.setName(dto.getName());
        categoryRepository.save(existingCategory);
        return modelMapper.map(existingCategory, CategoryDTO.class);
    }

    public void deleteCategoryById(long id) {
        categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));
        categoryRepository.deleteById(id);
    }

    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }

    private void validateCategoryName(String category){
        if(category == null || category.equals("null")) {
            throw new BadRequestException("Category can not be null.");
        }
        if(category.length() < 3 || category.isBlank() || category.length() > 10) {
            throw new BadRequestException("Category name must be between 3 and 10 letters");
        }
        List<Category> categories = categoryRepository.findAll();
        for (Category c : categories) {
            if(category.equals(c.getName())) {
                throw new BadRequestException("Category name already exists.");
            }
        }
    }

}
