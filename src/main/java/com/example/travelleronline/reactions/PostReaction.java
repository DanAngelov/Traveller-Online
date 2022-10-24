package com.example.travelleronline.reactions;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "post_reactions")
@Data
public class PostReaction {

    @EmbeddedId
    PostReactionsKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    Post post;

    boolean isLike;

}