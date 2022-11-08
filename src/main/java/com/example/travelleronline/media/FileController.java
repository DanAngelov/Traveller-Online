package com.example.travelleronline.media;

import com.example.travelleronline.general.MasterController;
import com.example.travelleronline.general.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController extends MasterController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/{uri}")
    @SneakyThrows
    public void downloadFile(@PathVariable String uri, HttpServletResponse response) {
        File file = fileService.downloadFile(uri);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @PutMapping("/users/image")
    public void changeProfileImage(@RequestParam(value = "image") MultipartFile image,
                                   HttpSession session) {
        int uid = getUserId(session);
        fileService.changeProfileImage(uid, image);
    }

    @PostMapping("/posts/{pid}/image")
    public void uploadPostImage(@PathVariable int pid, @RequestParam MultipartFile image,
                                HttpSession session) {
        int uid = getUserId(session);
        fileService.uploadPostImage(uid, pid, image);
    }

    @DeleteMapping("/posts/{pid}/images/{id}")
    public void deletePostImageById(@PathVariable int pid, @PathVariable int id,
                                    HttpSession session) {
        int uid = getUserId(session);
        fileService.deletePostImageById(uid, pid, id);
    }

    @DeleteMapping("/posts/{pid}/all-images")
    public void deleteAllPostImages(@PathVariable int pid, HttpSession session) {
        int uid = getUserId(session);
        fileService.deleteAllPostImages(uid, pid);
    }

    @PostMapping("/posts/{pid}/video")
    public void uploadPostVideo(@PathVariable int pid,@RequestParam MultipartFile video,
                                HttpSession session) {
        int uid = getUserId(session);
        fileService.uploadPostVideo(uid, pid, video);
    }

    @DeleteMapping("/posts/{pid}/video")
    public void deletePostVideo(@PathVariable int pid, HttpSession session) {
        int uid = getUserId(session);
        fileService.deletePostVideo(uid, pid);
    }

}