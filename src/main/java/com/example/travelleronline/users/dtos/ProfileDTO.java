package com.example.travelleronline.users.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private char gender;
    private String userPhotoUri;

}