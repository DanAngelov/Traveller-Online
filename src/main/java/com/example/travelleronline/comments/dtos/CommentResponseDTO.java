package com.example.travelleronline.comments.dtos;

import com.example.travelleronline.users.dtos.UserCommentDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;
    private UserCommentDTO user;
    private CommentDTO parent;

}
