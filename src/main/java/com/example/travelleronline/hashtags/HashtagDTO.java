package com.example.travelleronline.hashtags;

import lombok.Data;

import java.util.List;

@Data
public class HashtagDTO {

    private int hashtagId;
    private String name;
    private List<Integer> postsIds;

}
