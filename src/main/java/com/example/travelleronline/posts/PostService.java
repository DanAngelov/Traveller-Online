package com.example.travelleronline.posts;

import com.example.travelleronline.comments.Comment;
import com.example.travelleronline.hashtags.Hashtag;
import com.example.travelleronline.media.PostImage;
import com.example.travelleronline.categories.Category;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.users.User;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService extends MasterService {

    public PostDTO createPost(PostDTO dto, int userId) {
        Category c = validatePost(dto);
        LocalDateTime time = LocalDateTime.now();
        dto.setDateOfUpload(time);
        Post p = new Post();
        User u = userRepository.findById(1).orElseThrow(() -> new NotFoundException("User not found"));//TODO setting 1 for testing purpose
        p.setOwner(u);
        p.setDateOfUpload(time);
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setLocationLatitude(dto.getLocationLatitude());
        p.setLocationLongitude(dto.getLocationLongitude());
        p.setCategory(c);
        dto.setOwnerId(1);
        postRepository.save(p);
        dto.setPostId(p.getPostId());
        //TODO check why this not working
        c.getPosts().add(p);
        categoryRepository.save(c);
        return dto;
    }

    public List<PostDTO> getAllPosts() {
        List<PostDTO> dtos = new ArrayList<>();
        for (Post p : postRepository.findAll()){
            PostDTO dto = mapPostToDTO(p);
            dtos.add(dto);
        }
        return dtos;
    }

    private PostDTO mapPostToDTO(Post p) {
        PostDTO dto = new PostDTO();
        dto.setPostId(p.getPostId());
        dto.setOwnerId(p.getOwner().getUserId());
        dto.setCategory(p.getCategory().getName());
        dto.setDescription(p.getDescription());
        dto.setTitle(p.getTitle());
        dto.setDateOfUpload(p.getDateOfUpload());
        dto.setLocationLatitude(p.getLocationLatitude());
        dto.setLocationLongitude(p.getLocationLongitude());
        dto.setClipUri(p.getClipUri());
        List<String> images = new ArrayList<>();
        for (PostImage i : p.getPostImages()) {
            images.add(i.getImageUri());
        }
        dto.setPostImages(images);
        List<String> comments = new ArrayList<>();
        for (Comment c : p.getComments()) {
            comments.add(c.getContent());
        }
        dto.setComments(comments);
        List<Integer> taggedUsers = new ArrayList<>();
        for (User u : p.getTaggedUsers()) {
            taggedUsers.add(u.getUserId());
        }
        dto.setTaggedUsersIds(taggedUsers);
        return dto;
    }

    public PostDTO getPostById(int id) {
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        return mapPostToDTO(p);
    }

    public void deletePostById(int id) { //TODO check who is deleting(owner/admin/otherUser)
        postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        postRepository.deleteById(id);
    }

    public void deleteAllPosts() {
        postRepository.deleteAll();
    }

    //TODO Keep this function or not ?
//    public PostDTO editPost(int id, PostDTO dto) { //TODO check who is editing(owner/admin/otherUser)
//        validatePost(dto);
//        Post existingPost = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
//        existingPost.setTitle(dto.getTitle());
//        existingPost.setDescription(dto.getDescription());
//        existingPost.setCategory(dto.getCategory());
//        existingPost.setClipUri(dto.getClipUri());
//        existingPost.setLocationLatitude(dto.getLocationLatitude());
//        existingPost.setLocationLongitude(dto.getLocationLongitude());
//        postRepository.save(existingPost);
//        return modelMapper.map(existingPost, PostDTO.class);
//    }

    private Category validatePost(PostDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        Category c = validateCategory(dto.getCategory());
        validateLocation(dto.getLocationLatitude(), dto.getLocationLongitude());
        return c;
    }

    private void validateLocation(double locationLatitude, double locationLongitude) {
        if (locationLongitude < -180 || locationLongitude > 180 || locationLatitude < -90 || locationLatitude > 90) {
            throw new BadRequestException("Location latitude must be a number between -90 and 90 and the longitude between -180 and 180.");
        }
    }

    private Category validateCategory(String category) {
        if (categoryRepository.findByName(category) != null) {
            return categoryRepository.findByName(category);
        }
        throw new BadRequestException("No such category.");
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
        if (title.length() < 3 || title.isBlank() || title.length() > 100) {
            throw new BadRequestException("Title must be between 3 and 100 letters");
        }
        List<Post> posts = postRepository.findAll();
        for (Post p : posts) {
            if (p.getTitle().equals(title)) {
                throw new BadRequestException("Title already exists.");
            }
        }
    }

    public void tagUserToPost(int pid, int uid) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
        p.getTaggedUsers().add(u);
        u.getTaggedInPosts().add(p);
        postRepository.save(p);
        userRepository.save(u);
    }

    public PostDTO getPostByTitle(String title) {
        Post p = postRepository.findByTitle(title);
        if (p == null) {
            throw new NotFoundException("No such post.");
        }
        return mapPostToDTO(p);
    }

    public void addHashtagToPost(int pid, String hashtag) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        Hashtag tag = hashtagRepository.findByName(hashtag);
        if (tag == null) {
            Hashtag newTag = new Hashtag();
            newTag.setName(hashtag);
            if(p.getPostHashtags().contains(newTag)) {
                throw new BadRequestException("Hashtag already included in post.");
            }
            hashtagRepository.save(newTag);
        }
        if(p.getPostHashtags().contains(tag)) {
            throw new BadRequestException("Hashtag already included in post.");
        }
        p.getPostHashtags().add(tag);
        postRepository.save(p);
    }
}
