package com.example.travelleronline.hashtags;

import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.general.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class HashtagController extends MasterController {

    @Autowired
    HashtagService hashtagService;

    @PostMapping("/posts/{pid}/hashtags")
    public void addHashtagToPost(@PathVariable int pid,
                                 @RequestBody HashtagDTO hashtag,
                                 HttpSession session){
        int uid = getUserId(session);
        hashtagService.addHashtagToPost(pid, hashtag, uid);
    }

    @DeleteMapping("/posts/{pid}/hashtags")
    public void deleteHashtagFromPost(@PathVariable int pid,
                                      @RequestBody HashtagDTO hashtag,
                                      HttpSession session){
        int uid = getUserId(session);
        hashtagService.deleteHashtagFromPost(pid, hashtag, uid);
    }

}
