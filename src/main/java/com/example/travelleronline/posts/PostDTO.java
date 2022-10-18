package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.users.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostDTO {

    private long id;
    private User owner;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private Category categoryId;
    private float locationLatitude;
    private float locationLongitude;

}
