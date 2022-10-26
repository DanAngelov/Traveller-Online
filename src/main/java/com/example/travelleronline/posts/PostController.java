package com.example.travelleronline.posts;

import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class PostController extends MasterController {

    @Autowired
    private PostService postService;

    @PostMapping(value = "/posts")
    public PostCreationDTO createPost(@RequestBody PostCreationDTO dto, HttpSession session){
        int uid = getUserId(session);
        return postService.createPost(dto, uid);
    }

    @GetMapping(value = "/posts/{uid}")
    public List<PostDTO> getAllPostsOfUser(@PathVariable int uid){
        return postService.getPostsOfUser(uid);
    }

    @GetMapping(value = "/posts/titles/{title}")
    public List<PostDTO> getPostsByTitle(@PathVariable String title){
        return postService.getPostsByTitle(title);
    } // TODO should be refactored -> one endpoint (Dan)

    @GetMapping(value = "/posts/hashtags/{hashtag}")
    public List<PostDTO> getPostsByHashtag(@PathVariable String hashtag){
        return postService.getPostsByHashtag(hashtag);
    } // TODO should be refactored -> one endpoint (Dan)

    @GetMapping(value = "/posts/categories/{category}")
    public List<PostDTO> getPostsByCategory(@PathVariable String category){
        return postService.getPostsByCategory(category);
    }

    @DeleteMapping(value = "/posts/{pid}")
    public void deletePostById(@PathVariable int pid, HttpSession session){
        int uid = getUserId(session);
        postService.deletePostById(pid, uid);
    }

    @PostMapping(value = "/posts/{pid}/tag/{uid}")
    public void tagUserToPost(@PathVariable int pid, @PathVariable int uid) {
        postService.tagUserToPost(pid,uid);
    }

    @PostMapping(value = "/posts/{pid}/{hashtag}")
    public void addHashtagToPost(@PathVariable int pid, @PathVariable String hashtag,HttpSession session){
        int uid = getUserId(session);
        postService.addHashtagToPost(pid, hashtag, uid);
    }

    @PutMapping("/posts/{pid}")
    public void editPost(@PathVariable int pid, @RequestBody PostCreationDTO dto, HttpSession session){
        int uid = getUserId(session);
        postService.editPost(pid, dto, uid);
    }

    // News Feed
//    @GetMapping("/news-feed") // TODO ? infinite scroll is correct
//    public List<PostDTO> showNewsFeed(HttpServletRequest req,
//                                      @RequestParam("days_min") int daysMin, // TODO ? ??? List<PostDTO>
//                                      @RequestParam("days_max") int daysMax) {
//        return postService.showNewsFeed(userController.getUserId(req), daysMin, daysMax);
//    }

    // Profile Page
//    @GetMapping("/users/{uid}/posts") // TODO ? infinite scroll is correct ??? List<PostDTO>
//    public List<PostDTO> showPostsOfUser(@PathVariable int uid,
//                                         @RequestParam("days_min") int daysMin,
//                                         @RequestParam("days_max") int daysMax,
//                                         @RequestParam("order_by") String orderBy) {
//        return postService.showPostsOfUser(uid, daysMin, daysMax, orderBy);
//    }

    @PutMapping("/posts/{pid}/react")
    public LikesDislikesDTO reactTo(@PathVariable int pid, @RequestParam("reaction") String reaction, HttpSession session) {
        int uid = (int) session.getAttribute(USER_ID);
        return postService.reactTo(uid, pid, reaction);
    }

    @GetMapping("/posts/{pid}/users")
    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(@PathVariable int pid, @RequestParam("reaction") String reaction) {
        return postService.getUsersWhoReacted(pid, reaction);
    }

    //TODO Keep this function or not ?
//    @PutMapping("/posts/{id}")
//    public PostDTO editPost(@PathVariable int id, @RequestBody PostDTO dto){
//        return postService.editPost(id, dto);
//    }


}
