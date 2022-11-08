package com.example.travelleronline.hashtags;

import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.general.MasterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HashtagService extends MasterService {

    private static final int HASHTAG_LENGTH_MIN = 2;
    private static final int HASHTAG_LENGTH_MAX = 50;

    @Transactional
    void addHashtagToPost(Post post, HashtagDTO hashtag) {
        validateHashtagName(hashtag.getName());
        for (Hashtag g : post.getPostHashtags()) {
            if(g.getName().equals(hashtag.getName())) {
                throw new BadRequestException("The hashtag is already included in the post.");
            }
        }
        Hashtag tag = hashtagRepository.findByName(hashtag.getName());
        if (tag == null) {
            tag = new Hashtag();
            tag.setName(hashtag.getName());
            hashtagRepository.save(tag);
        }
        post.getPostHashtags().add(tag);
        postRepository.save(post);
    }

    void deleteHashtagFromPost(int pid, HashtagDTO hashtag, int uid) {
        Post post = validatePostOwner(pid, uid);
        for (Hashtag tag : post.getPostHashtags()) {
            if(tag.getName().equals(hashtag.getName())) {
                post.getPostHashtags().remove(tag);
                postRepository.save(post);
                return;
            }
        }
        throw new BadRequestException("No such hashtag in the post.");
    }

    private void validateHashtagName(String name) {
        if (name == null || name.isBlank() ||
        name.length() < HASHTAG_LENGTH_MIN || name.length() > HASHTAG_LENGTH_MAX) {
            throw new BadRequestException("Hashtag's name should be between " +
                    HASHTAG_LENGTH_MIN + " and " + HASHTAG_LENGTH_MAX + " symbols.");
        }
    }

}