package com.example.travelleronline.media;

import com.example.travelleronline.general.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@RestController
public class FileController extends MasterController {

    @Autowired
    private FileService fileService;

    @PostMapping("/posts/{pid}/image")
    public void uploadImage(@PathVariable int pid, @RequestParam MultipartFile image, HttpSession session){
        int uid = getUserId(session);
        fileService.uploadImage(pid, image, uid);
    }

    @DeleteMapping("/posts/{pid}/images")
    public void deleteAllPostImages(@PathVariable int pid, HttpSession session){
        int uid = getUserId(session);
        fileService.deleteAllPostImages(pid, uid);
    }

    @PostMapping("/posts/{pid}/video")
    public void uploadVideo(@PathVariable int pid,@RequestParam MultipartFile video, HttpSession session){
        int uid = getUserId(session);
        fileService.uploadVideo(pid, video, uid);
    }

    @PutMapping("/users/change-image")
    public void changeProfileImage(@RequestParam(value = "image") MultipartFile image, HttpSession session) {
        fileService.changeProfileImage(getUserId(session), image);
    }

}
