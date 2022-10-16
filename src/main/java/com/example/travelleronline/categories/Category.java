package com.example.travelleronline.categories;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "post_categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;

}
