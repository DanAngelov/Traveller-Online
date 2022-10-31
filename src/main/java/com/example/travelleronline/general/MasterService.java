package com.example.travelleronline.general;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.categories.CategoryRepository;
import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.comments.CommentRepository;
import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.general.exceptions.UnauthorizedException;
import com.example.travelleronline.hashtags.HashtagRepository;
import com.example.travelleronline.media.PostImageRepository;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.posts.PostRepository;
import com.example.travelleronline.reactions.toComment.CommentReactionRepository;
import com.example.travelleronline.reactions.toPost.PostReactionRepository;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

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
    protected static final String DEF_PROFILE_IMAGE_URI = "uploads" + File.separator +
            "def_profile_image.png";

    protected User getVerifiedUserById(int uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (!user.isVerified()) {
            throw new BadRequestException("The user is not verified.");
        }
        return user;
    }

    protected Post getPostById(int pid) {
        return postRepository.findById(pid)
                .orElseThrow(() -> new NotFoundException("Post not found."));
    }

    protected Category getCategoryById(int cid) {
        return categoryRepository.findById(cid)
                .orElseThrow(() -> new NotFoundException("Category not found."));
    }

    protected Comment getCommentById(int cid) {
        return commentRepository.findById(cid)
                .orElseThrow(() -> new NotFoundException("Comment not found."));
    }

    protected Post validatePostOwner(int pid, int uid) {
        Post post = getPostById(pid);
        if(post.getOwner().getUserId() != uid) {
            throw new UnauthorizedException("You are not the post owner");
        }
        return post;
    }

}