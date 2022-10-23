package com.example.travelleronline.posts;

import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.media.PostImage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {

    private int postId;
    private int ownerId;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private String category;
    private double locationLatitude;
    private double locationLongitude;
    private List<String> comments;
    private List<String> postImages;
    private List<Integer> taggedUsersIds;

}
