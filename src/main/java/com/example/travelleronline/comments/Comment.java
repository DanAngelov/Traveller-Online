package com.example.travelleronline.comments;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.toComment.CommentReaction;
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
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "comment")
    private List<CommentReaction> commentReactions;

}