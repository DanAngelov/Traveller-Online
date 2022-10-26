package com.example.travelleronline.posts;

import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.posts.dtos.PostWithoutOwnerDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController extends MasterController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/posts")
    public PostCreationDTO createPost(@RequestBody PostCreationDTO dto){
       return postService.createPost(dto);
    }

    @GetMapping(value = "/posts/{uid}")
    public List<PostWithoutOwnerDTO> getAllPostsOfUser(@PathVariable int uid){
        return postService.getPostsOfUser(uid);
    }

    @GetMapping(value = "/posts/titles/{title}")
    public List<PostWithoutOwnerDTO> getPostsByTitle(@PathVariable String title){
        return postService.getPostsByTitle(title);
    }

    @GetMapping(value = "/posts/hashtags/{hashtag}")
    public List<PostWithoutOwnerDTO> getPostsByHashtag(@PathVariable String hashtag){
        return postService.getPostsByHashtag(hashtag);
    }

    @GetMapping(value = "/posts/categories/{category}")
    public List<PostWithoutOwnerDTO> getPostsByCategory(@PathVariable String category){
        return postService.getPostsByCategory(category);
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

    @PutMapping("/posts/{id}")
    public void editPost(@PathVariable int id, @RequestBody PostCreationDTO dto){
        postService.editPost(id, dto);
    }
    // News Feed
//    @GetMapping("/news-feed") // TODO ? infinite scroll is correct
//    public List<PostDTO> showNewsFeed(HttpServletRequest req,
//                                      @RequestParam("days_min") int daysMin, // TODO ? ??? List<PostDTO>
//                                      @RequestParam("days_max") int daysMax) {
//        return postService.showNewsFeed(userController.getUserId(req), daysMin, daysMax);
//    }

//    // Profile Page
//    @GetMapping("/users/{uid}/posts") // TODO ? infinite scroll is correct ??? List<PostDTO>
//    public List<PostDTO> showPostsOfUser(@PathVariable int uid,
//                                         @RequestParam("days_min") int daysMin,
//                                         @RequestParam("days_max") int daysMax,
//                                         @RequestParam("order_by") String orderBy) {
//        return postService.showPostsOfUser(uid, daysMin, daysMax, orderBy);
//    }


}
