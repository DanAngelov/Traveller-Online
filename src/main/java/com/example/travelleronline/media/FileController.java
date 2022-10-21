package com.example.travelleronline.media;

import com.example.travelleronline.util.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController extends MasterController {

    @Autowired
    private FileService fileService;

    @PostMapping("/posts/{pid}/image")
    public void uploadImage(@PathVariable int pid, @RequestParam MultipartFile file){
        fileService.uploadImage(pid,file);
    }

    @PostMapping("/posts/{pid}/video")
    public void uploadVideo(@PathVariable int pid,@RequestParam MultipartFile file){
        fileService.uploadVideo(pid,file);
    }

    @PostMapping("/users/{uid}/image")
    public void changeProfileImage(@PathVariable int uid, @RequestParam MultipartFile file){
        fileService.changeProfileImage(uid, file);
    }

}
