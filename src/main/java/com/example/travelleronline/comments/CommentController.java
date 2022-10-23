package com.example.travelleronline.comments;

import com.example.travelleronline.users.UserController;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping()
public class CommentController extends MasterController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserController userController;

    @GetMapping(value = "/posts/{pid}/comments")
    public List<CommentDTO> getAllPostComments(@PathVariable int pid) {
        return commentService.getAllPostComments(pid);
    }
    @PostMapping("/posts/{pid}/comments")
    public CommentDTO createComment(@PathVariable int pid, @RequestBody CommentDTO dto, HttpServletRequest req){
        int userId = userController.getUserId(req);
        return commentService.createComment(pid,dto,userId);
    }
    //TODO fix not working
    @PutMapping("/posts/{pid}/comments/{cid}")
    public CommentDTO editComment(@PathVariable int pid, @PathVariable int cid, @RequestBody CommentDTO dto){
        return commentService.editComment(pid,cid,dto);
    }
    @DeleteMapping("/posts/{pid}/comments/{cid}")
    public void deleteComment(@PathVariable int pid, @PathVariable int cid){
        commentService.deleteComment(pid,cid);
    }
    @DeleteMapping("/posts/{pid}/comments")
    public void deleteAllComments(@PathVariable int pid){
        commentService.deleteAllComments(pid);
    }
}
