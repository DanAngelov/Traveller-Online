package com.example.travelleronline.posts;

import com.example.travelleronline.hashtags.Hashtag;
import com.example.travelleronline.categories.Category;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.posts.dtos.PostWithoutOwnerDTO;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserWithoutPostDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends MasterService {

    public PostCreationDTO createPost(PostCreationDTO dto) {
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
        c.getPosts().add(p);//TODO check
        categoryRepository.save(c);
        return dto;
    }

    public List<PostWithoutOwnerDTO> getAllPostsOfUser(int uid) {
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found."));
        UserWithoutPostDTO dto = modelMapper.map(u, UserWithoutPostDTO.class);
        dto.setPosts(u.getPosts().stream().map(p -> modelMapper.map(p, PostWithoutOwnerDTO.class)).collect(Collectors.toList()));
        return dto.getPosts();
    }

    public void deletePostById(int id) { //TODO check who is deleting(owner/admin/otherUser)
        postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        postRepository.deleteById(id);
    }

    public void deleteAllPosts() {
        postRepository.deleteAll();
    }

    public void editPost(int id, PostCreationDTO dto) { //TODO check who is editing(owner/admin/otherUser)
        Category c = validatePost(dto);
        Post existingPost = postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found."));
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        existingPost.setCategory(c);
        existingPost.setLocationLatitude(dto.getLocationLatitude());
        existingPost.setLocationLongitude(dto.getLocationLongitude());
        postRepository.save(existingPost);
    }

    private Category validatePost(PostCreationDTO dto) {
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
    }

    public void tagUserToPost(int pid, int uid) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
        p.getTaggedUsers().add(u);
        u.getTaggedInPosts().add(p);
        postRepository.save(p);
        userRepository.save(u);
    }

    public List<PostWithoutOwnerDTO> getPostsByTitle(String title) {
        List<Post> posts = postRepository.findAllByTitle(title);
        return posts.stream().map(p -> modelMapper.map(p,PostWithoutOwnerDTO.class)).collect(Collectors.toList());
    }

    public void addHashtagToPost(int pid, String hashtag) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        for (Hashtag g : p.getPostHashtags()) {
            if(g.getName().equals(hashtag)) {
                throw new BadRequestException("Hashtag already included in post");
            }
        }
        Hashtag tag = new Hashtag();
        tag.setName(hashtag);
        hashtagRepository.save(tag);
        p.getPostHashtags().add(tag);
        postRepository.save(p);
    }

    public List<PostWithoutOwnerDTO> getPostsByHashtag(String hashtag) {
        Hashtag tag = validateHashtag(hashtag);
        List<Post> posts = postRepository.findAllByPostHashtags(tag);
        return posts.stream().map(p -> modelMapper.map(p,PostWithoutOwnerDTO.class)).collect(Collectors.toList());
    }

    private Hashtag validateHashtag(String hashtag) {
        if (hashtagRepository.findByName(hashtag) != null) {
            return hashtagRepository.findByName(hashtag);
        }
        throw new BadRequestException("No such hashtag.");
    }

    public List<PostWithoutOwnerDTO> getPostsByCategory(String category) {
        Category c = validateCategory(category);
        List<Post> posts = postRepository.findAllByCategory(c);
        return posts.stream().map(p -> modelMapper.map(p,PostWithoutOwnerDTO.class)).collect(Collectors.toList());
    }

}
