package com.example.travelleronline.posts;

import com.example.travelleronline.hashtags.Hashtag;
import com.example.travelleronline.media.PostImage;
import com.example.travelleronline.categories.Category;
import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.users.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;
    @Column
    private String title;
    @Column
    private LocalDateTime dateOfUpload;
    @Column
    private String clipUri;
    @Column
    private String description;
    @Column
    private double locationLatitude;
    @Column
    private double locationLongitude;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @OneToMany(mappedBy = "post")
    private List<PostImage> postImages;
    @ManyToMany(mappedBy = "taggedInPosts")
    private List<User> taggedUsers;
    @ManyToMany
    @JoinTable(
            name = "postHashtags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> postHashtags;
    @OneToMany(mappedBy = "post")
    List<PostReaction> postReactions;

}