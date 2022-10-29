package com.example.travelleronline.users;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    List<User> findAllByEmail (String email);

    List<User> findAllByPhoneNumber(String phone);

    List<User> findAllByFirstNameLikeAndLastNameLike(String firstName, String lastName,
                                                     Pageable page);

    List<User> findAllByFirstNameLikeOrLastNameLike(String firstName, String lastName,
                                                    Pageable page);

    List<User> findAllByIsVerified (boolean isVerified);

}