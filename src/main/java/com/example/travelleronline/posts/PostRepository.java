package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer> {

    List<Post> findAllByCategory(Category category, Pageable page);


}