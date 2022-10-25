package com.example.travelleronline.users.dtos;

import com.example.travelleronline.posts.dtos.PostWithoutOwnerDTO;
import lombok.Data;

import java.util.List;

@Data
public class UserWithoutPostDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private String userPhotoUri;
    private boolean isVerified;
    private List<PostWithoutOwnerDTO> posts;

}
