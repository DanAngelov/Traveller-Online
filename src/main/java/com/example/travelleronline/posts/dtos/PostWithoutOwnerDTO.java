package com.example.travelleronline.posts.dtos;

import com.example.travelleronline.categories.dtos.CategoryDTO;
import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.media.PostImageDTO;
import com.example.travelleronline.reactions.PostReaction;
import com.example.travelleronline.users.dtos.UserWithoutPassDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostWithoutOwnerDTO {

    private int postId;
    private String title;
    private LocalDateTime dateOfUpload;
    private String clipUri;
    private String description;
    private double locationLatitude;
    private double locationLongitude;
    private CategoryDTO category;
    private List<CommentDTO> comments;
    private List<PostImageDTO> postImages;
    private List<UserWithoutPassDTO> taggedUsers;
    private List<HashtagDTO> postHashtags;
    private List<PostReaction> postReactions;//TODO future me GL HF
}
