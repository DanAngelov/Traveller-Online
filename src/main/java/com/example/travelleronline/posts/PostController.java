package com.example.travelleronline.posts;

import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController extends MasterController {

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create")
    public void createPost(@RequestBody Post p){
        p.setDateOfUpload(LocalDateTime.now());
        postRepository.save(p);
        //TODO validate
    }
    @GetMapping()
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable long id){
        return postRepository.findById(id).orElseThrow(); //TODO make exception return proper status
    }

    @DeleteMapping(value = "/{id}", headers = "password=4kd2!kd7@SE1")
    public void deletePostById(@PathVariable long id){
        //TODO validate
        postRepository.deleteById(id);
    }
    @DeleteMapping(headers = "password=4kd2!kd7@SE1")
    public void deleteAllPosts(){
        postRepository.deleteAll();
    }
    @PutMapping("/{id}")
    public void editPost(@PathVariable long id, @RequestBody Post p){
        //TODO validate data
        Post post = postRepository.findById(id).orElseThrow(); //TODO make exception return proper status
        post.setClipUri(p.getClipUri());
        post.setDescription(p.getDescription());
        post.setTitle(p.getTitle());
        post.setCategoryId(p.getCategoryId());
        postRepository.save(post);
    }



}
