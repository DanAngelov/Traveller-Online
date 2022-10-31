package com.example.travelleronline.media;

import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import com.example.travelleronline.general.MasterService;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            throw new BadRequestException("You are not the post owner.");
        }
        return post;
    }

    private void validateImage(MultipartFile image) {
        if (image.getContentType().equals("image/jpeg") || image.getContentType().equals("image/jpg")
        || image.getContentType().equals("image/png")) {
            return;
        }
        throw new BadRequestException("File type needs to be jpg,jpeg or png.");
    };
    private void validateVideo(MultipartFile video){
        if (video == null) {
            throw new BadRequestException("Video not uploaded.");
        }
        if(video.getContentType().equals("video/mp4") || video.getContentType().equals("video/avi")
            || video.getContentType().equals("video/x-msvideo")) {
            return;
        }
        throw new BadRequestException("File type needs to be mp4 or avi.");
    };

    public void uploadPostVideo(int pid, MultipartFile file, int uid) {
        validateVideo(file);
        Post post = validatePost(pid, uid);
        if(post.getClipUri() != null) {
            deleteOldFile(post.getClipUri());
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
        String oldImageURI = user.getUserPhotoUri();
        if (file == null) {
            if (!oldImageURI.equals(DEF_PROFILE_IMAGE_URI)) {
                deleteOldFile(oldImageURI);
            }
            user.setUserPhotoUri(DEF_PROFILE_IMAGE_URI);
            return;
        }
        validateImage(file);
        if(oldImageURI != null && !oldImageURI.equals(DEF_PROFILE_IMAGE_URI)) {
            deleteOldFile(user.getUserPhotoUri());
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
        Post post = validatePost(pid,uid);
        for (PostImage image : post.getPostImages()) {
            deleteOldFile(image.getImageUri());
        }
        postImageRepository.deleteAllByPost(post);
    }

    public void deletePostVideo(int pid, int uid) {
        Post post = validatePost(pid,uid);
        deleteOldFile(post.getClipUri());
        post.setClipUri(null);
        postRepository.save(post);
    }

    @SneakyThrows
    private void deleteOldFile(String uri) {
        Files.delete(Path.of(uri));
    }

}