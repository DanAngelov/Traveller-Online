package com.example.travelleronline.users;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column //TODO not blank
    private String firstName;
    @Column //TODO not blank
    private String lastName;
    @Column //TODO not blank; valid email?
    private String email; //TODO unique
    @Column //TODO not blank; valid phone?
    private String phone; //TODO hashed value?
    @Column //TODO not blank
    private String password;
    @Column //TODO not null
    private LocalDate dateOfBirth;
    @Column
    private char gender;
    @Column
    private LocalDateTime createdAt; //TODO DateTime
    @Column
    private String userPhotoUri;
    @Column(columnDefinition = "TINYINT(1)")  //TODO should be 0
    private boolean isVerified;

}