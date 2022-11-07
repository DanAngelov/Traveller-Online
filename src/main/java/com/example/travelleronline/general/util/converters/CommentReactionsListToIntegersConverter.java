package com.example.travelleronline.general.util.converters;

import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
import com.example.travelleronline.reactions.toComment.CommentReaction;
import org.modelmapper.AbstractConverter;

import java.util.List;
import java.util.stream.Collectors;

public class CommentReactionsListToIntegersConverter
        extends AbstractConverter<List<CommentReaction>, LikesDislikesDTO> {

    @Override
    protected LikesDislikesDTO convert(List<CommentReaction> reactions) {
        LikesDislikesDTO dto = new LikesDislikesDTO();
        if(reactions != null) {
            int likes = reactions.stream()
                    .filter(pr -> pr.isLike())
                    .collect(Collectors.toList())
                    .size();
            dto.setLikes(likes);
            int dislikes = reactions.size() - likes;
            dto.setDislikes(dislikes);
        } else {
            dto.setLikes(0);
            dto.setDislikes(0);
        }
        return dto;
    }

}