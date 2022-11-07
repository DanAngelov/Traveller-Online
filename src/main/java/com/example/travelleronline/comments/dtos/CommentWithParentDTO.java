package com.example.travelleronline.comments.dtos;

import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithParentDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;
    private UserIdNamesPhotoDTO user;
    private CommentWithoutParentDTO parent;
    private LikesDislikesDTO reactions;

}