package com.example.travelleronline.comments;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService extends MasterService {
    public CommentDTO createComment(int pid, CommentDTO dto, int id) {
        //TODO validate user
        validatePostId(pid);
        validateComment(dto);
        LocalDateTime time = LocalDateTime.now();
        dto.setCreatedAt(time);
        Comment c = new Comment();
        c.setCreatedAt(time);
        c.setContent(dto.getContent());
        c.setParentId(dto.getParentId());
        User u = userRepository.findById(2).orElseThrow(() -> new NotFoundException("No such user."));//TODO get user from session
        c.setUser(u);
        Post p = postRepository.findById(dto.getPostId()).orElseThrow(() -> new NotFoundException("No such post."));
        c.setPost(p);
        commentRepository.save(c);
        dto.setCommentId(c.getCommentId());
        return dto;
    }

    private CommentDTO mapCommentToCommentDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(c.getCommentId());
        dto.setContent(c.getContent());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setOwnerId(c.getUser().getUserId());
        dto.setPostId(c.getPost().getPostId());
        dto.setParentId(c.getParentId());
        return dto;
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
        if (dto.getContent().length() < 5 || dto.getContent().length() > 500) {
            throw new BadRequestException("Comment size must be between 5 and 500 letters.");
        }
    }

    private void validateCommentId(int cid) {
        commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("Comment not found."));
    }

    public List<CommentDTO> getAllPostComments(int pid) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        List<CommentDTO> dtos = new ArrayList<>();
        for (Comment c : p.getComments()) {
            CommentDTO dto = mapCommentToCommentDTO(c);
            dtos.add(dto);
        }
        return dtos;
    }
}
