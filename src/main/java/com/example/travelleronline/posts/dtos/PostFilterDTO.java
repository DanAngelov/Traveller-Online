package com.example.travelleronline.posts.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostFilterDTO {

    private int postId;
    private String category;
    private String title;
    private String userFullName;

}