package com.example.travelleronline.posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

//    @PostMapping()
//    public PostDTO createPost(@RequestBody PostDTO p){
//       return postService.createPost();
//    }
    @GetMapping()
    public List<PostDTO> getAllPosts(){
        return postService.getAllPosts();
    }
    @GetMapping("/{id}")
    public PostDTO getPostById(@PathVariable long id){
        return postService.getPostById(id);
    }
    @DeleteMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public void deletePostById(@PathVariable long id){
        postService.deletePostById(id);
    }
    @DeleteMapping(headers = "password=4kd2!kd7@SE1")
    public void deleteAllPosts(){
        postService.deleteAllPosts();
    }
//    @PutMapping("/{id}")
//    public PostDTO editPost(@PathVariable long id, @RequestBody PostDTO dto){
//        return postService.editPost(id, dto);
//    }



}
