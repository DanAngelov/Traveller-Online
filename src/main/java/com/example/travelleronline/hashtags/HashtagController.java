package com.example.travelleronline.hashtags;

import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class HashtagController extends MasterController {

    @Autowired
    HashtagService hashtagService;

    @PostMapping(value = "/posts/{pid}")
    public void addHashtagToPost(@PathVariable int pid, @RequestBody HashtagDTO hashtag, HttpSession session){
        int uid = getUserId(session);
        hashtagService.addHashtagToPost(pid, hashtag, uid);
    }

}
