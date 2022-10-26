package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.comments.dtos.CommentRequestDTO;
import com.example.travelleronline.comments.dtos.CommentResponseDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutPostDTO;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserCommentDTO;
import com.example.travelleronline.users.dtos.UserProfileDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends MasterService {
    public CommentResponseDTO createComment(int pid, CommentDTO dto) {
        //TODO validate if user is logged in and take user id from session.
        validatePostId(pid);
        validateComment(dto);
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("No such post."));
        User u = userRepository.findById(1).orElseThrow(() -> new NotFoundException("No such user."));//TODO get user from session
        Comment c = new Comment();
        c.setCreatedAt(LocalDateTime.now());
        c.setContent(dto.getContent());
        c.setUser(u);
        c.setPost(p);
        commentRepository.save(c);
        return modelMapper.map(c, CommentResponseDTO.class);
    }

    public void editComment(int pid, int cid, CommentDTO dto) {
        validatePostId(pid);
        validateComment(dto);
        Comment existingComment = commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("Comment not found."));
        existingComment.setContent(dto.getContent());
        commentRepository.save(existingComment);
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

    public List<CommentWithoutPostDTO> getPostComments(int pid) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        List<Comment> postComments = p.getComments();
        return postComments.stream().map(c -> modelMapper.map(c, CommentWithoutPostDTO.class)).collect(Collectors.toList());
    }

    public CommentResponseDTO respondToComment(int pid, int cid, int uid, CommentRequestDTO dto) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
        Comment c = commentRepository.getCommentByPostAndAndCommentId(p,cid);
        Comment response = new Comment();
        response.setPost(p);
        response.setCreatedAt(LocalDateTime.now());
        response.setContent(dto.getContent());
        response.setUser(u);
        response.setParent(c);
        commentRepository.save(response);
        return modelMapper.map(response,CommentResponseDTO.class);
    }
}
