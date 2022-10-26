package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.hashtags.Hashtag;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer> {

    List<Post> findAllByTitle(String title);
    List<Post> findAllByPostHashtags(Hashtag tag);

    List<Post> findAllByCategory(Category category);
}
