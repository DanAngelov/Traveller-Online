package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.posts.dtos.PostWithoutCategoryDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends MasterService {

    public PostDTO createPost(PostDTO dto) {
        validatePost(dto);
        dto.setDateOfUpload(LocalDateTime.now());
        Post p = modelMapper.map(dto,Post.class);
        postRepository.save(p);
        return dto;
    }

    public List<PostWithoutCategoryDTO> getAllPosts() {
        return postRepository.findAll().stream().map((p -> modelMapper.map(p, PostWithoutCategoryDTO.class))).collect(Collectors.toList());
    }

    public PostDTO getPostById(int id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        return modelMapper.map(p, PostDTO.class);
    }

    public void deletePostById(int id) { //TODO check who is deleting(owner/admin/otherUser)
        postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        postRepository.deleteById(id);
    }

    public void deleteAllPosts() {
        postRepository.deleteAll();
    }

    public PostDTO editPost(int id, PostDTO dto) { //TODO check who is editing(owner/admin/otherUser)
        Post p = modelMapper.map(dto, Post.class);
        validatePost(dto);
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        existingPost.setCategory(dto.getCategory());
        existingPost.setClipUri(dto.getClipUri());
        existingPost.setLocationLatitude(dto.getLocationLatitude());
        existingPost.setLocationLongitude(dto.getLocationLongitude());
        postRepository.save(existingPost);
        return modelMapper.map(existingPost, PostDTO.class);
    }

    private void validatePost(PostDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateCategory(dto.getCategory());
        validateLocation(dto.getLocationLatitude(), dto.getLocationLongitude());
    }

    private void validateLocation(double locationLatitude, double locationLongitude) {
        if (locationLongitude < -180 || locationLongitude > 180 || locationLatitude < -90 || locationLatitude > 90) {
            throw new BadRequestException("Location latitude must be a number between -90 and 90 and the longitude between -180 and 180.");
        }
    }

    private void validateCategory(Category category) {
        categoryRepository.findById(category.getCategoryId()).orElseThrow(() -> new BadRequestException("No such category."));
    }

    private void validateDescription(String desc) {
        if (desc.length() > 500) {
            throw new BadRequestException("Description must be between 0 and 500 letters.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.equals("null")) {
            throw new BadRequestException("Title can not be null.");
        }
        if (title.length() < 3 || title.isBlank() || title.length() > 10) {
            throw new BadRequestException("Title must be between 3 and 100 letters");
        }
        List<Post> posts = postRepository.findAll();
        for (Post p : posts) {
            if (p.getTitle().equals(title)) {
                throw new BadRequestException("Title already exists.");
            }
        }
    }

}
