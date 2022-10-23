package com.example.travelleronline.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;
    private int parentId;
    private int postId;
    private int ownerId;

}