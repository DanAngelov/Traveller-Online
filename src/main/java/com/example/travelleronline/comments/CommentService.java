package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentRequestDTO;
import com.example.travelleronline.comments.dtos.CommentResponseDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutPostDTO;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.reactions.toComment.CommentReaction;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends MasterService {

    public CommentResponseDTO createComment(int pid, CommentRequestDTO dto, int uid) {
        validatePostId(pid);
        validateComment(dto);
        Post p = getPostById(pid);
        User u = getVerifiedUserById(uid);
        Comment c = new Comment();
        c.setCreatedAt(LocalDateTime.now());
        c.setContent(dto.getContent());
        c.setUser(u);
        c.setPost(p);
        commentRepository.save(c);
        return modelMapper.map(c, CommentResponseDTO.class);
    }

    public void editComment(int pid, int cid, CommentRequestDTO dto,int uid) {
        validateOwnerOfComment(uid, cid);
        validatePostId(pid);
        validateComment(dto);
        Comment existingComment = getCommentById(cid);
        existingComment.setContent(dto.getContent());
        commentRepository.save(existingComment);
    }

    private void validateOwnerOfComment(int uid, int cid) {
        Comment c = getCommentById(cid);
        User u = getVerifiedUserById(uid);
        if(!c.getUser().equals(u)) {
            throw new UnauthorizedException("Only the owner of the comment can edit the comment.");
        }
    }

    public void deleteComment(int pid, int cid, int uid) {
        validatePostId(pid);
        validateCommentId(cid);
        if(validateDeletionOfComment(pid, cid, uid)) {
            commentRepository.deleteById(cid);
        }
        else {
            throw new BadRequestException("You must be post owner or comment owner to delete this comment.");
        }
    }

    private boolean validateDeletionOfComment(int pid, int cid, int uid) {
        Post p = getPostById(pid);
        Comment c = getCommentById(cid);
        User sessionUser = getVerifiedUserById(uid);
        User postOwner = p.getOwner();
        User commentOwner = c.getUser();
        if (sessionUser.equals(commentOwner) || sessionUser.equals(postOwner)) {
            return true;
        }
        return false;
    }

    public void deleteAllComments(int pid, int uid) {
        validatePostId(pid);
        validateDeletion(pid, uid);
        commentRepository.deleteAll();
    }

    private void validateDeletion(int pid, int uid) {
        Post p = getPostById(pid);
        User postOwner = p.getOwner();
        User sessionUser = getVerifiedUserById(uid);
        if(!sessionUser.equals(postOwner)) {
            throw new UnauthorizedException("You must be the post owner to delete all comments.");
        }
    }

    private void validatePostId(int pid) {
        postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
    }

    private void validateComment(CommentRequestDTO dto) {
        if (dto.getContent().length() < 5 || dto.getContent().length() > 500) {
            throw new BadRequestException("Comment size must be between 5 and 500 letters.");
        }
    }

    private void validateCommentId(int cid) {
        commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("Comment not found."));
    }

    public List<CommentWithoutPostDTO> getPostComments(int pid) {
        Post p = getPostById(pid);
        List<Comment> postComments = p.getComments();
        return postComments.stream().map(c -> modelMapper.map(c, CommentWithoutPostDTO.class)).collect(Collectors.toList());
    }

    public CommentResponseDTO respondToComment(int pid, int cid, int uid, CommentRequestDTO dto) {
        Post p = getPostById(pid);
        User u = getVerifiedUserById(uid);
        Comment c = getCommentById(cid);
        Comment response = new Comment();
        response.setPost(p);
        response.setCreatedAt(LocalDateTime.now());
        response.setContent(dto.getContent());
        response.setUser(u);
        response.setParent(c);
        commentRepository.save(response);
        return modelMapper.map(response,CommentResponseDTO.class);
    }

    public LikesDislikesDTO reactTo(int uid, int cid, String reaction) {
        User user = getVerifiedUserById(uid);
        Comment comment = getCommentById(cid);
        CommentReaction commentReaction = new CommentReaction();
        commentReaction.setUser(user);
        commentReaction.setComment(comment);
        if (reaction.equals("like")) {
            commentReaction.setLike(true);
        }
        else if (reaction.equals("dislike")) {
            commentReaction.setLike(false);
        }
        else {
            throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
        List<CommentReaction> reactionsSameCommentAndUser =
                commentReactRepo.findAllByUserAndComment(user, comment);
        if (reactionsSameCommentAndUser.size() == 0) {
            commentReactRepo.save(commentReaction);
        }
        else {
            CommentReaction oldCommentReaction = reactionsSameCommentAndUser.get(0);
            commentReactRepo.delete(oldCommentReaction);
            if (oldCommentReaction.isLike() != commentReaction.isLike()) {
                commentReactRepo.save(commentReaction);
            }
        }
        LikesDislikesDTO dto = new LikesDislikesDTO();
        dto.setLikes(comment.getCommentReactions().stream()
                .filter(pr -> pr.isLike())
                .collect(Collectors.toList())
                .size());
        dto.setDislikes(comment.getCommentReactions().stream()
                .filter(pr -> !pr.isLike())
                .collect(Collectors.toList())
                .size()); // TODO ? can be refactored or not
        return dto;
    }

    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(int cid, String reaction) {
        Comment comment = getCommentById(cid);
        if (reaction.equals("like")) {
            return comment.getCommentReactions().stream()
                    .filter(cr -> cr.isLike())
                    .map(cr -> modelMapper.map(cr.getUser(), UserIdNamesPhotoDTO.class))
                    .collect(Collectors.toList());
        }
        else if (reaction.equals("dislike")) {
            return comment.getCommentReactions().stream()
                    .filter(cr -> !cr.isLike())
                    .map(cr -> modelMapper.map(cr.getUser(), UserIdNamesPhotoDTO.class))
                    .collect(Collectors.toList());
        }
        else {
            throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
    }
}
