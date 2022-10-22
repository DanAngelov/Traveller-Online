package com.example.travelleronline.util;

import com.example.travelleronline.categories.CategoryRepository;
import com.example.travelleronline.comments.CommentRepository;
import com.example.travelleronline.media.PostImageRepository;
import com.example.travelleronline.posts.PostRepository;
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

}