package com.example.travelleronline.reactions.toPost;

import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Integer> {

    List<PostReaction> findAllByUserAndPost(User user, Post post);

}