package com.example.travelleronline.posts;

import com.example.travelleronline.users.UserController;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PostController extends MasterController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserController userController;

    @PostMapping(value = "/posts")
    public PostDTO createPost(@RequestBody PostDTO dto, HttpServletRequest req){
       int userId = userController.getUserId(req);
       return postService.createPost(dto, userId);
    }
    @GetMapping(value = "/posts")
    public List<PostDTO> getAllPosts(){
        return postService.getAllPosts();
    }
    @GetMapping(value = "/posts/{id}")
    public PostDTO getPostById(@PathVariable int id){
        return postService.getPostById(id);
    }
    @GetMapping(value = "/posts/title/{title}")
    public PostDTO getPostByTitle(@PathVariable String title){
        return postService.getPostByTitle(title);
    }
    @DeleteMapping(value = "/posts/{id}", headers = "password=4kd2!kd7@SE1")
    public void deletePostById(@PathVariable int id){
        postService.deletePostById(id);
    }
    @DeleteMapping(value = "/posts", headers = "password=4kd2!kd7@SE1")
    public void deleteAllPosts(){
        postService.deleteAllPosts();
    }
    @PostMapping(value = "/posts/{pid}/tag/{uid}")
    public void tagUserToPost(@PathVariable int pid, @PathVariable int uid) {
        postService.tagUserToPost(pid,uid);
    }

    @PostMapping(value = "/posts/{pid}/{hashtag}")
    public void addHashtagToPost(@PathVariable int pid, @PathVariable String hashtag){
        postService.addHashtagToPost(pid,hashtag);
    }

    //TODO Keep this function or not ?
//    @PutMapping("/posts/{id}")
//    public PostDTO editPost(@PathVariable int id, @RequestBody PostDTO dto){
//        return postService.editPost(id, dto);
//    }



}
