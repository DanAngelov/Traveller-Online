package com.example.travelleronline.hashtags;

import com.example.travelleronline.posts.PostDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HashtagController extends MasterController {

    @Autowired
    HashtagService hashtagService;

    @PostMapping(value = "/hashtags")
    public HashtagDTO createHashtag(@RequestBody HashtagDTO dto) {
        return hashtagService.createHashtag(dto);
    }

    //TODO make work...
//    @GetMapping(value = "/hashtags/{hashtag}")
//    public List<PostDTO> getAllPostsWithHashtag(@PathVariable String hashtag) {
//        return hashtagService.getAllPostsWithHashtag(hashtag);
//    }

}
