package com.example.travelleronline.comments;

import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.comments.dtos.CommentRequestDTO;
import com.example.travelleronline.comments.dtos.CommentWithParentDTO;
import com.example.travelleronline.general.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping()
public class CommentController extends MasterController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{pid}/comments")
    public CommentWithParentDTO createComment(@PathVariable int pid,
                                              @RequestBody CommentRequestDTO dto, HttpSession session) {
        int uid = getUserId(session);
        return commentService.createComment(pid, dto, uid);
    }

    @PutMapping("/comments/{cid}")
    public void editComment(@PathVariable int cid, @RequestBody CommentRequestDTO dto,
                            HttpSession session) {
        int uid = getUserId(session);
        commentService.editComment(cid, dto, uid);
    }

    @DeleteMapping("/comments/{cid}")
    public void deleteComment(@PathVariable int cid, HttpSession session) {
        int uid = getUserId(session);
        commentService.deleteComment(cid, uid);
    }

    @DeleteMapping("/posts/{pid}/comments")
    public void deleteAllComments(@PathVariable int pid, HttpSession session) {
        int uid = getUserId(session);
        commentService.deleteAllComments(pid, uid);
    }

    @PostMapping("/comments/{cid}")
    public CommentWithParentDTO respondToComment(@PathVariable int cid,
                                 @RequestBody CommentRequestDTO dto, HttpSession session) {
        int uid = getUserId(session);
        return commentService.respondToComment(cid, uid, dto);
    }

    // removes old reaction after following visit
    @PutMapping("/comments/{cid}/react")
    public LikesDislikesDTO reactTo(@PathVariable int cid,
                                    @RequestParam String reaction, HttpSession session) {
        int uid = getUserId(session);
        return commentService.reactTo(uid, cid, reaction);
    }

    @GetMapping("/comments/{cid}/users")
    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(@PathVariable int cid,
                                     @RequestParam String reaction) {
        return commentService.getUsersWhoReacted(cid, reaction);
    }

}