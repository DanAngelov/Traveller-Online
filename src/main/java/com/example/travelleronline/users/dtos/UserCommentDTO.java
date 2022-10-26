package com.example.travelleronline.users.dtos;

import lombok.Data;


@Data
public class UserCommentDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private String userPhotoUri;

}
