package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutPostDTO;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.reactions.toComment.CommentReaction;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends MasterService {
    public CommentDTO createComment(int pid, CommentDTO dto) {
        //TODO validate if user is logged in and take user id from session.
        validatePostId(pid);
        validateComment(dto);
        Comment c = new Comment();
        c.setCreatedAt(LocalDateTime.now());
        c.setContent(dto.getContent());
        User u = userRepository.findById(1).orElseThrow(() -> new NotFoundException("No such user."));//TODO get user from session
        c.setUser(u);
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("No such post."));
        c.setPost(p);
        commentRepository.save(c);
        return dto;
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

    public LikesDislikesDTO reactTo(int uid, int cid, String reaction) {
        User user = getVerifiedUserById(uid);
        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
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
        Comment comment = commentRepository.findById(cid)
                .orElseThrow(() -> new NotFoundException("Comment not found."));
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
