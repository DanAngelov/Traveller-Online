package com.example.travelleronline.reactions.toComment;

import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.users.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "comment_reactions")
@Data
public class CommentReaction {

    @EmbeddedId
    CommentReactionsKey id = new CommentReactionsKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    Comment comment;

    boolean isLike;

}