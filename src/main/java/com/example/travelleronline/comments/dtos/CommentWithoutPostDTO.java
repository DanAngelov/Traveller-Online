package com.example.travelleronline.comments.dtos;

import com.example.travelleronline.users.dtos.UserWithoutPassDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithoutPostDTO {

    private int commentId;
    private LocalDateTime createdAt;
    private String content;
    private CommentDTO parentId;//TODO future me GL HF
    private UserWithoutPassDTO user;

}
