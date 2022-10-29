package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.example.travelleronline.users.User;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer> {

    List<Post> findAllByCategory(Category category, Pageable page);

    Post getPostByOwnerAndPostId(User owner, int postId);
}
