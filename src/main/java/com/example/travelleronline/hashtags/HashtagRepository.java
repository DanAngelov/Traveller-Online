package com.example.travelleronline.hashtags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashtagRepository extends JpaRepository<Hashtag,Integer> {

    Hashtag findByName(String name);
}
