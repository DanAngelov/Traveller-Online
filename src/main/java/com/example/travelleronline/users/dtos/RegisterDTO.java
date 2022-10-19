package com.example.travelleronline.users.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private LocalDate dateOfBirth;
    private char gender;

}