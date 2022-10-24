package com.example.travelleronline.hashtags;

import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HashtagController extends MasterController {

    @Autowired
    HashtagService hashtagService;

    @PostMapping(value = "/hashtags")
    public HashtagDTO createHashtag(@RequestBody HashtagDTO dto) {
        return hashtagService.createHashtag(dto);
    }


}
