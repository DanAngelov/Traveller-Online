package com.example.travelleronline.hashtags;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.hashtags.dtos.HashtagDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashtagService extends MasterService {

    public HashtagDTO createHashtag(HashtagDTO dto) {
        validateHashtag(dto.getName());
        Hashtag hashtag = new Hashtag();
        hashtag.setName(dto.getName());
        hashtagRepository.save(hashtag);
        return dto;
    }

    private void validateHashtag(String hashtagName) {
        if(hashtagName == null || hashtagName.equals("null")) {
            throw new BadRequestException("Hashtag can not be null.");
        }
        if(hashtagName.length() < 3 || hashtagName.isBlank() || hashtagName.length() > 20) {
            throw new BadRequestException("Hashtag name must be between 3 and 20 letters");
        }
        Hashtag hashtag = hashtagRepository.findByName(hashtagName);
        if (hashtag == null) {
            throw new BadRequestException("Hashtag name already exists.");
        }
    }

}
