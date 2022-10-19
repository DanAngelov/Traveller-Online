package com.example.travelleronline.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByEmail (String email);
    List<User> findAllByPhone (String email);

}