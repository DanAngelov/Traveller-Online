package com.example.travelleronline.users.dtos;

import lombok.Data;

@Data
public class EditPassDTO {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}