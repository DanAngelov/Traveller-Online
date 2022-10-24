package com.example.travelleronline.comments.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;

}
