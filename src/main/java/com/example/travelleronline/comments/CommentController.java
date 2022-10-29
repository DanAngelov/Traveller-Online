package com.example.travelleronline.comments;

import com.example.travelleronline.reactions.LikesDislikesDTO;
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

    @GetMapping(value = "/posts/{pid}/comments")
    public List<CommentWithParentDTO> getPostComments(@PathVariable int pid) {
        return commentService.getPostComments(pid);
    }

    @PostMapping(value = "/posts/{pid}/comments/{cid}")
    public CommentWithParentDTO respondToComment(@PathVariable int pid, @PathVariable int cid, @RequestBody CommentRequestDTO dto, HttpSession session){
        int uid = getUserId(session);
        return commentService.respondToComment(pid, cid, uid, dto);
    }

    @PostMapping("/posts/{pid}/comments")
    public CommentWithParentDTO createComment(@PathVariable int pid, @RequestBody CommentRequestDTO dto, HttpSession session){
        int uid = getUserId(session);
        return commentService.createComment(pid, dto, uid);
    }

    @PutMapping("/posts/{pid}/comments/{cid}")
    public void editComment(@PathVariable int pid, @PathVariable int cid, @RequestBody CommentRequestDTO dto, HttpSession session){
        int uid = getUserId(session);
        commentService.editComment(pid,cid, dto, uid);
    }

    @DeleteMapping("/posts/{pid}/comments/{cid}")
    public void deleteComment(@PathVariable int pid, @PathVariable int cid, HttpSession session){
        int uid = getUserId(session);
        commentService.deleteComment(pid, cid, uid);
    }

    @DeleteMapping("/posts/{pid}/comments")
    public void deleteAllComments(@PathVariable int pid, HttpSession session){
        int uid = getUserId(session);
        commentService.deleteAllComments(pid, uid);
    }

    @PutMapping("/comments/{cid}/react")
    public LikesDislikesDTO reactTo(@PathVariable int cid, @RequestParam("reaction") String reaction, HttpSession session) {
        int uid = getUserId(session);
        return commentService.reactTo(uid, cid, reaction);
    }

    @GetMapping("/comments/{cid}/users")
    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(@PathVariable int cid, @RequestParam("reaction") String reaction) {
        return commentService.getUsersWhoReacted(cid, reaction);
    }

}
