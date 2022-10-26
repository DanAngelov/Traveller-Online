package com.example.travelleronline.hashtags;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;



@Service
public class HashtagService extends MasterService {

    public void addHashtagToPost(int pid, HashtagDTO hashtag, int uid) {
        Post p = validatePostOwner(pid, uid);
        for (Hashtag g : p.getPostHashtags()) {
            if(g.getName().equals(hashtag.getName())) {
                throw new BadRequestException("Hashtag already included in post");
            }
        }
        Hashtag tag = hashtagRepository.findByName(hashtag.getName());
        if (tag == null) {
            tag = new Hashtag();
            tag.setName(hashtag.getName());
            hashtagRepository.save(tag);
        }
        p.getPostHashtags().add(tag);
        postRepository.save(p);
    }



}
