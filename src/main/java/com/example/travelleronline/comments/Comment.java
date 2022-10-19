package com.example.travelleronline.comments;

import com.example.travelleronline.posts.Post;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    //TODO check if correctly mapped
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName= "posts")
    private Post postId;
    @Column
    private LocalDateTime createdAt;
    @Column
    private String content;
    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName= "comments")
    private Post parentId;
}
