package com.example.travelleronline.categories;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {

    private int categoryId;
    private String name;
    private List<Integer> postIds;

}
