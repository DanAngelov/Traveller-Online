package com.example.travelleronline.comments;

import com.example.travelleronline.comments.dtos.CommentDTO;
import com.example.travelleronline.comments.dtos.CommentRequestDTO;
import com.example.travelleronline.comments.dtos.CommentResponseDTO;
import com.example.travelleronline.comments.dtos.CommentWithoutPostDTO;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class CommentController extends MasterController {

    @Autowired
    private CommentService commentService;

    @GetMapping(value = "/posts/{pid}/comments")
    public List<CommentWithoutPostDTO> getPostComments(@PathVariable int pid) {
        return commentService.getPostComments(pid);
    }

    @PostMapping(value = "/posts/{pid}/comments/{cid}")
    public CommentResponseDTO respondToComment(@PathVariable int pid, @PathVariable int cid,@RequestBody CommentRequestDTO dto){
        int uid = 4;//TODO fix hard coding
        return commentService.respondToComment(pid, cid, uid, dto);
    }

    @PostMapping("/posts/{pid}/comments")
    public CommentResponseDTO createComment(@PathVariable int pid, @RequestBody CommentDTO dto){
        return commentService.createComment(pid,dto);
    }

    @PutMapping("/posts/{pid}/comments/{cid}")
    public void editComment(@PathVariable int pid, @PathVariable int cid, @RequestBody CommentDTO dto){
        commentService.editComment(pid,cid,dto);
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
