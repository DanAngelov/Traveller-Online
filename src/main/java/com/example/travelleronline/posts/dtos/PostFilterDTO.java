package com.example.travelleronline.posts.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class PostFilterDTO implements Serializable {

    private int postId;
    private String category;
    private String title;
    private String userFullName;
    private double locationLatitude;
    private double locationLongitude;

}