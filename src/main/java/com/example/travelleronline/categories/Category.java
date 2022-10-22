package com.example.travelleronline.categories;

import com.example.travelleronline.posts.Post;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "post_categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;
    @Column
    private String name;
    @OneToMany(mappedBy = "category")
    private List<Post> posts;

}