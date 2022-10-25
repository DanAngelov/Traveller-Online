package com.example.travelleronline.util;

import com.example.travelleronline.categories.CategoryRepository;
import com.example.travelleronline.comments.CommentRepository;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.hashtags.HashtagRepository;
import com.example.travelleronline.media.PostImageRepository;
import com.example.travelleronline.posts.PostRepository;
import com.example.travelleronline.reactions.toComment.CommentReactionRepository;
import com.example.travelleronline.reactions.toPost.PostReactionRepository;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class MasterService {

    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PostImageRepository postImageRepository;
    @Autowired
    protected HashtagRepository hashtagRepository;
    @Autowired
    protected PostReactionRepository postReactRepo;
    @Autowired
    protected CommentReactionRepository commentReactRepo;

    protected User getVerifiedUserById(int uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (!user.isVerified()) {
            throw new BadRequestException("The user is not verified.");
        }
        return user;
    }

//    protected Post getPostById(int pid) {
//        return postRepository.findById(pid)
//                .orElseThrow(() -> new NotFoundException("Post not found."));
//    } // TODO use everywhere

}