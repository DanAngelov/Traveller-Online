package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{pid}")
public class CommentController extends MasterController {

    @Autowired
    private CommentService commentService;
    @PostMapping
    public CommentDTO createComment(@PathVariable int pid, @RequestBody CommentDTO dto){
        return commentService.createComment(pid,dto);
    }
    @PutMapping("/{cid}")
    public CommentDTO editComment(@PathVariable int pid, @PathVariable int cid, @RequestBody CommentDTO dto){
        return commentService.editComment(pid,cid,dto);
    }
    @DeleteMapping("/{cid}")
    public void deleteComment(@PathVariable int pid, @PathVariable int cid){
        commentService.deleteComment(pid,cid);
    }
    @DeleteMapping
    public void deleteAllComments(@PathVariable int pid){
        commentService.deleteAllComments(pid);
    }
}
