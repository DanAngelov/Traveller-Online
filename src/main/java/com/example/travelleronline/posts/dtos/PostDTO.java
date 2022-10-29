package com.example.travelleronline.posts.dtos;

import com.example.travelleronline.categories.dtos.CategoryDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutParentDTO;
import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.media.PostImageDTO;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.users.dtos.UserProfileDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {

    private int postId;
    private String title;
    private LocalDateTime dateOfUpload;
    private UserProfileDTO owner;
    private String clipUri;
    private String description;
    private double locationLatitude;
    private double locationLongitude;
    private CategoryDTO category;
    private List<CommentWithoutParentDTO> comments;
    private List<PostImageDTO> postImages;
    private List<UserIdNamesPhotoDTO> taggedUsers;
    private List<HashtagDTO> postHashtags;
    private LikesDislikesDTO reactions;

}
