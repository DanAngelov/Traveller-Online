package com.example.travelleronline.media;

import com.example.travelleronline.users.UserController;
import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FileController extends MasterController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserController userController;

    @PostMapping("/posts/{pid}/image")
    public void uploadImage(@PathVariable int pid, @RequestParam MultipartFile file){
        fileService.uploadImage(pid,file);
    }

    @PostMapping("/posts/{pid}/video")
    public void uploadVideo(@PathVariable int pid,@RequestParam MultipartFile file){
        fileService.uploadVideo(pid,file);
    }

    @PostMapping("/users/change-image")
    public void changeProfileImage(@RequestParam(value = "image") MultipartFile image, HttpServletRequest req) {
        userController.validateLoggedIn(req);
        fileService.changeProfileImage(userController.getUserId(req), image);
    }

}
