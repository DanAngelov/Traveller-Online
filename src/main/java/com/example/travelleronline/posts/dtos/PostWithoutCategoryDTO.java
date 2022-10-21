package com.example.travelleronline.posts.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostWithoutCategoryDTO {

    private int postId;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private double locationLatitude;
    private double locationLongitude;

}
