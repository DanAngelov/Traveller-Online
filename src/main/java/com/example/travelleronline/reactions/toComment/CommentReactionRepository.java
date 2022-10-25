package com.example.travelleronline.reactions.toComment;

import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Integer> {

    List<CommentReaction> findAllByUserAndComment(User user, Comment comment);

}