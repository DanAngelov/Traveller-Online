package com.example.travelleronline.comments.dtos;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;
    private int parentId;
    private Post postId;
    private User user;

}
