package com.example.travelleronline.media;

import com.example.travelleronline.posts.Post;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "post_pictures")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;
    @Column
    private String imageUri;
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;



}
