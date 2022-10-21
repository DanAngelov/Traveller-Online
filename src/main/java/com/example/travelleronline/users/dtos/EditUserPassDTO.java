package com.example.travelleronline.users.dtos;

import lombok.Data;

@Data
public class EditUserPassDTO {

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}