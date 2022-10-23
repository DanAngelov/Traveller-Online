package com.example.travelleronline.posts.dtos;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.users.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {

    private int postId;
    private User ownerId;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
//    private Category category; TODO bring back
    private double locationLatitude;
    private double locationLongitude;

}
