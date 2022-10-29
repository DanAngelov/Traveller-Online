package com.example.travelleronline.media;

import com.example.travelleronline.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Integer> {

    void deleteAllByPost(Post p);

}
