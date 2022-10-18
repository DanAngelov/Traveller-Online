package com.example.travelleronline.posts;

import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.posts.dtos.PostDTONoOwner;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController extends MasterController {

    @Autowired
    private PostService postService;

    @PostMapping()
    public PostDTO createPost(@RequestBody Post p){
       return postService.createPost(p);
    }
    @GetMapping()
    public List<PostDTONoOwner> getAllPosts(){
        return postService.getAllPosts();
    }
    @GetMapping("/{id}")
    public PostDTO getPostById(@PathVariable int id){
        return postService.getPostById(id);
    }
    @DeleteMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public void deletePostById(@PathVariable int id){
        postService.deletePostById(id);
    }
    @DeleteMapping(headers = "password=4kd2!kd7@SE1")
    public void deleteAllPosts(){
        postService.deleteAllPosts();
    }
    @PutMapping("/{id}")
    public PostDTO editPost(@PathVariable int id, @RequestBody PostDTO dto){
        return postService.editPost(id, dto);
    }



}
