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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class FileService extends MasterService {

    public static final int MAX_NUMBERS_POST_IMAGES = 3;

    File downloadFile(String uri) {
        File file = new File(uri);
        if (!file.exists()) {
            throw new NotFoundException("File not found!");
        }
        return file;
    }
    @SneakyThrows
    void changeProfileImage(int uid, MultipartFile image) {
        User user = getVerifiedUserById(uid);
        String oldImageURI = user.getUserPhotoUri();
        if(oldImageURI != null && !oldImageURI.equals(DEF_PROFILE_IMAGE_URI)) {
            deleteOldFile(user.getUserPhotoUri());
        }
        if (image == null) {
            user.setUserPhotoUri(DEF_PROFILE_IMAGE_URI);
            return;
        }
        validateImage(image);
        String uri = saveFile(image);
        user.setUserPhotoUri(uri);
        userRepository.save(user);
    }

    @SneakyThrows
    void uploadPostImage(int uid, int pid, MultipartFile image) {
        Post post = validatePostOwner(pid, uid);
        validateImage(image);
        List<PostImage> postImages = post.getPostImages();
        if (postImages.size() == MAX_NUMBERS_POST_IMAGES) {
            throw new BadRequestException("You can have a maximum of " + MAX_NUMBERS_POST_IMAGES +
                    " images per post.");
        }
        String uri = saveFile(image);
        PostImage postImage = new PostImage();
        postImage.setImageUri(uri);
        postImage.setPost(post);
        postImageRepository.save(postImage);
    }

    public void deletePostImageById(int uid, int pid, int id) {
        Post post = validatePostOwner(pid,uid);
        PostImage image = getPostImageById(id);
        if (image.getPost() != post) {
            throw new BadRequestException("The image is not of this post.");
        }
        deleteOldFile(image.getImageUri());
        postImageRepository.delete(image);
    }

    @Transactional
    void deleteAllPostImages(int uid, int pid) {
        Post post = validatePostOwner(pid,uid);
        for (PostImage image : post.getPostImages()) {
            deleteOldFile(image.getImageUri());
        }
        postImageRepository.deleteAllByPost(post);
    }

    @SneakyThrows
    void uploadPostVideo(int uid, int pid, MultipartFile video) {
        Post post = validatePostOwner(pid, uid);
        validateVideo(video);
        if(post.getClipUri() != null) {
            deleteOldFile(post.getClipUri());
        }
        String uri = saveFile(video);
        post.setClipUri(uri);
        postRepository.save(post);
    }

    void deletePostVideo(int uid, int pid) {
        Post post = validatePostOwner(pid,uid);
        deleteOldFile(post.getClipUri());
        post.setClipUri(null);
        postRepository.save(post);
    }

    private void validateImage(MultipartFile image) {
        if (image == null) {
            throw new BadRequestException("Video not uploaded.");
        }
        if (image.getContentType().equals("image/jpeg") || image.getContentType().equals("image/jpg")
                || image.getContentType().equals("image/png")) {
            return;
        }
        throw new BadRequestException("File type needs to be jpg,jpeg or png.");
    }

    private void validateVideo(MultipartFile video){
        if (video == null) {
            throw new BadRequestException("Video not uploaded.");
        }
        if(video.getContentType().equals("video/mp4") || video.getContentType().equals("video/avi")
                || video.getContentType().equals("video/x-msvideo")) {
            return;
        }
        throw new BadRequestException("File type needs to be mp4 or avi.");
    }

    @SneakyThrows
    private String saveFile(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uri = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(uri);
        Files.copy(file.getInputStream(), f.toPath());
        return uri;
    }

    @SneakyThrows
    private void deleteOldFile(String uri) {
        Files.delete(Path.of(uri));
    }

}