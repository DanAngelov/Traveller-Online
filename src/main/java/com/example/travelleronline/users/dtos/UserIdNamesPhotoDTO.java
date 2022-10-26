package com.example.travelleronline.users.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserIdNamesPhotoDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private String userPhotoUri;

}