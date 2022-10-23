package com.example.travelleronline.users.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserWithoutPassDTO {

    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private char gender;
    private LocalDateTime createdAt;
    private String userPhotoUri;
    private boolean isVerified;

}