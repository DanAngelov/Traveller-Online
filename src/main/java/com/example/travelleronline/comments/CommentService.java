package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService extends MasterService {
    public CommentDTO createComment(int pid, CommentDTO dto) {
        validatePostId(pid);
        validateComment(dto);
        dto.setCreatedAt(LocalDateTime.now());
        Comment comment = modelMapper.map(dto, Comment.class);
        commentRepository.save(comment);
        return modelMapper.map(comment,CommentDTO.class);
    }

    public CommentDTO editComment(int pid, int cid, CommentDTO dto) {
        validatePostId(pid);
        validateComment(dto);
        Comment existingComment = commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("Comment not found."));
        existingComment.setContent(dto.getContent());
        commentRepository.save(existingComment);
        return modelMapper.map(existingComment,CommentDTO.class);
    }

    public void deleteComment(int pid, int cid) {
        validatePostId(pid);
        validateCommentId(cid);
        commentRepository.deleteById(cid);
    }

    public void deleteAllComments(int pid) {
        validatePostId(pid);
        commentRepository.deleteAll();
    }

    private void validatePostId(int pid) {
        postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
    }

    private void validateComment(CommentDTO dto) {
        userRepository.findById(dto.getUser().getUserId()).orElseThrow(() -> new NotFoundException("No such user."));
        if (dto.getContent().length() < 5 || dto.getContent().length() > 500) {
            throw new BadRequestException("Comment size must be between 5 and 500 letters.");
        }
    }

    private void validateCommentId(int cid) {
        commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("Comment not found."));
    }
}
