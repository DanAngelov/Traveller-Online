package com.example.travelleronline.comments;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.CommentReaction;
import com.example.travelleronline.users.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;
    @Column
    private LocalDateTime createdAt;
    @Column
    private String content;
    @Column
    private int parentId;//TODO this might need to be Comment parentId;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "comment")
    List<CommentReaction> commentReactions;

}