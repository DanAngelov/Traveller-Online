package com.example.travelleronline.media;

import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import com.example.travelleronline.general.MasterService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class FileService extends MasterService {

    @Transactional
    public void uploadImage(int pid, MultipartFile file, int uid) {
        Post post = validatePost(pid, uid);
        validateImage(file);
        List<PostImage> postImages = post.getPostImages();
        if (postImages.size() > 2) {
            throw new BadRequestException("You can have a maximum of 3 images per post.");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(name);
        try {
            Files.copy(file.getInputStream(), f.toPath());
        }
        catch (IOException e) {
            throw new BadRequestException("File already exists.");
        }
        PostImage image = new PostImage();
        image.setImageUri(name);
        image.setPost(post);
        postImages.add(image);
        post.setPostImages(postImages);
        postImageRepository.save(image);
        postRepository.save(post);
    }

    private Post validatePost(int pid, int uid) {
        Post post = getPostById(pid);
        if(post.getOwner().getUserId() != uid) {
            throw new NotFoundException("Post not found.");
        }
        return post;
    }

    private void validateImage(MultipartFile file) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/jpg")
        || file.getContentType().equals("image/png")) {
            return;
        }
        throw new BadRequestException("File type needs to be jpg,jpeg or png.");
    };
    private void validateVideo(MultipartFile file){
        if(file.getContentType().equals("video/mp4") || file.getContentType().equals("video/avi")
            || file.getContentType().equals("video/x-msvideo")) {
            return;
        }
        throw new BadRequestException("File type needs to be mp4 or avi.");
    };

    public void uploadVideo(int pid, MultipartFile file, int uid) {
        validateVideo(file);
        Post post = validatePost(pid, uid);
        if(post.getClipUri() != null) {
            File oldClip = new File(post.getClipUri());
            oldClip.delete();
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(name);
        try {
            Files.copy(file.getInputStream(), f.toPath());
        }
        catch (IOException e) {
            throw new BadRequestException("File already exists.");
        }
        post.setClipUri(name);
        postRepository.save(post);
    }

    public void changeProfileImage(int uid, MultipartFile file) {
        User user = getVerifiedUserById(uid);
        validateImage(file);
        if(user.getUserPhotoUri() != null) {
            File oldImage = new File(user.getUserPhotoUri());
            oldImage.delete();
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(name);
        try {
            Files.copy(file.getInputStream(), f.toPath());
        }
        catch (IOException e) {
            throw new BadRequestException("File already exists.");
        }
        user.setUserPhotoUri(name);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAllPostImages(int pid, int uid) {
        Post p = validatePost(pid,uid);
        postImageRepository.deleteAllByPost(p);
    }
}
