package com.example.travelleronline.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByEmail (String email);

    List<User> findAllByPhone (String phone);

    List<User> findAllByFirstNameOrLastName(String name,String name1);

    List<User> findAllByFirstName(String name);

    List<User> findAllByLastName(String name);

    List<User> findAllByIsVerified (boolean isVerified);

}