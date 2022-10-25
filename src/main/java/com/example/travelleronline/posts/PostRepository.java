package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.hashtags.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findAllByTitle(String title);
    List<Post> findAllByPostHashtags(Hashtag tag);

    List<Post> findAllByCategory(Category category);
}
