package com.example.travelleronline.users;

import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.toComment.CommentReaction;
import com.example.travelleronline.reactions.toPost.PostReaction;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String email;
    @Column
    private String phone;
    @Column
    private String password;
    @Column
    private LocalDate dateOfBirth;
    @Column
    private char gender;
    @Column
    private String userPhotoUri;
    @Column
    private LocalDateTime createdAt;
    @Column
    private boolean isVerified;

    @ManyToMany
    @JoinTable(
        name = "subscribers",
        joinColumns = @JoinColumn(name = "user_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "sub_id", nullable = false)
    )
    List<User> subscribers;

    @ManyToMany(mappedBy = "subscribers")
    List<User> subscriptions;

    @OneToMany(mappedBy = "owner")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "taggedInPosts",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "post_id", nullable = false)
    )
    private List<Post> taggedInPosts;

    @OneToMany(mappedBy = "user")
    List<PostReaction> postReactions;

    @OneToMany(mappedBy = "user")
    List<CommentReaction> commentReactions;

}