package com.example.travelleronline.media;

import com.example.travelleronline.general.exceptions.BadRequestException;
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

    public static final int MAX_NUMBERS_POST_IMAGES = 3;

    @SneakyThrows
    void uploadPostImage(int pid, MultipartFile image, int uid) {
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

    private String saveFile(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uri = "uploads" + File.separator + System.nanoTime() + "." + extension;
        File f = new File(uri);
        Files.copy(file.getInputStream(), f.toPath());
        return uri;
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

    @SneakyThrows
    void uploadPostVideo(int pid, MultipartFile video, int uid) {
        Post post = validatePostOwner(pid, uid);
        validateVideo(video);
        if(post.getClipUri() != null) {
            deleteOldFile(post.getClipUri());
        }
        String uri = saveFile(video);
        post.setClipUri(uri);
        postRepository.save(post);
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

    @Transactional
    void deleteAllPostImages(int pid, int uid) {
        Post post = validatePostOwner(pid,uid);
        for (PostImage image : post.getPostImages()) {
            deleteOldFile(image.getImageUri());
        }
        postImageRepository.deleteAllByPost(post);
    }

    void deletePostVideo(int pid, int uid) {
        Post post = validatePostOwner(pid,uid);
        deleteOldFile(post.getClipUri());
        post.setClipUri(null);
        postRepository.save(post);
    }

    @SneakyThrows
    private void deleteOldFile(String uri) {
        Files.delete(Path.of(uri));
    }

}