package com.example.travelleronline.posts.dtos;

import com.example.travelleronline.categories.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostWithoutOwnerDTO {

    private int postId;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private Category category;
    private double locationLatitude;
    private double locationLongitude;

}