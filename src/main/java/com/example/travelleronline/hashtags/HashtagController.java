package com.example.travelleronline.hashtags;

import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.general.MasterController;
import com.example.travelleronline.posts.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class HashtagController extends MasterController {

    @Autowired
    HashtagService hashtagService;

    @PostMapping("/posts/{pid}/hashtags")
    public void addHashtagsToPost(@PathVariable int pid,
                                 @RequestBody List<HashtagDTO> hashtags,
                                 HttpSession session){
        int uid = getUserId(session);
        Post post = hashtagService.validatePostOwner(pid, uid);
        for (HashtagDTO hashtag : hashtags) {
            hashtagService.addHashtagToPost(post, hashtag);
        }
    }

    @DeleteMapping("/posts/{pid}/hashtags")
    public void deleteHashtagFromPost(@PathVariable int pid,
                                      @RequestBody HashtagDTO hashtag,
                                      HttpSession session){
        int uid = getUserId(session);
        hashtagService.deleteHashtagFromPost(pid, hashtag, uid);
    }

}
