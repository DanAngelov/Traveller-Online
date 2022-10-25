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
        dto.setHashtagId(hashtag.getHashtagId());
        return dto;
    }

    private void validateHashtag(String hashTagName) {
        if(hashTagName == null || hashTagName.equals("null")) {
            throw new BadRequestException("Hashtag can not be null.");
        }
        if(hashTagName.length() < 3 || hashTagName.isBlank() || hashTagName.length() > 20) {
            throw new BadRequestException("Hashtag name must be between 3 and 20 letters");
        }
        List<Hashtag> hashtags = hashtagRepository.findAll();
        for (Hashtag h : hashtags) {
            if(hashTagName.equals(h.getName())) {
                throw new BadRequestException("Hashtag name already exists.");
            }
        }
    }

}
