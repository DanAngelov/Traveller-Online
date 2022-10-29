package com.example.travelleronline.posts.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostCreationDTO {

    private int postId;
    private String title;
    private String description;
    private String category;
    private LocalDateTime DateOfUpload;
    private double locationLatitude;
    private double locationLongitude;

}