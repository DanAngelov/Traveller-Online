package com.example.travelleronline.media;

import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.Post;
import com.example.travelleronline.users.User;
import com.example.travelleronline.util.MasterService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class FileService extends MasterService {

        public void uploadImage(int pid, MultipartFile file) {
        //TODO validate image type (mime)
        //TODO validate if user is uploading images to own post
        Post post = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        List<PostImage> postImages = post.getPostImages();
        if(postImages.size() > 2) {
            throw new BadRequestException("You can have only 3 images per post.");
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
        postImageRepository.save(image);
        postImages.add(image);
        post.setPostImages(postImages);
        postRepository.save(post);
    }

    public void uploadVideo(int pid, MultipartFile file) {
        Post post = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        //TODO validate video type (mime)
        //TODO validate if user is uploading video to own post
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
        User user = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found."));
        //TODO validate
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
}