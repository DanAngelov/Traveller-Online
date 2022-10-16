package com.example.travelleronline.posts;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int ownerId;
    @Column
    private String title;
    @Column
    private LocalDateTime dateOfUpload;
    @Column
    private String clipUri;
    @Column
    private String description;
    @Column
    private long categoryId;
    @Column
    private float locationLatitude;
    @Column
    private float locationLongitude;


    //TODO pictures, likes/unlikes, dislikes/undislikes,

}
