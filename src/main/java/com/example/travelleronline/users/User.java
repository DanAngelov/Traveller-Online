package com.example.travelleronline.users;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "sub_id")
    )
    List<User> subscribers;

    @ManyToMany(mappedBy = "subscribers")
    List<User> subscriptions;

}