package com.example.travelleronline.hashtags;

import com.example.travelleronline.posts.Post;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hashtagId;
    @Column
    private String name;
    @ManyToMany(mappedBy = "postHashtags")
    private List<Post> posts;

}