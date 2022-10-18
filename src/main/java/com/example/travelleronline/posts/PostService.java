package com.example.travelleronline.posts;

import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.util.MasterController;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    public void createPost(Post p){
//        validatePost(p);//TODO validate
        p.setDateOfUpload(LocalDateTime.now());
        postRepository.save(p);
    }

    public List<PostDTO> getAllPosts(){
        return postRepository.findAll().stream().map((p -> modelMapper.map(p,PostDTO.class))).collect(Collectors.toList());
    }

    public PostDTO getPostById(long id){
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        return modelMapper.map(p,PostDTO.class);
    }

    public void deletePostById(long id){ //TODO check who is deleting(owner/admin/otherUser)
        postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        postRepository.deleteById(id);
    }

    public void deleteAllPosts(){
        postRepository.deleteAll();
    }

//    public PostDTO editPost(long id, PostDTO dto){ //TODO check who is editing(owner/admin/otherUser)
//        Post existingPost = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
//        existingPost = modelMapper.map(dto,Post.class);
//        postRepository.save(existingPost);
//    }

}
