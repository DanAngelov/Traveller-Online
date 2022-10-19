package com.example.travelleronline.posts.dtos;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.users.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {

    private int id;
    private User owner;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private Category category;
    private double locationLatitude;
    private double locationLongitude;

}
