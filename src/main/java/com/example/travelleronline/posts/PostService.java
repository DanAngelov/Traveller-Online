package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.hashtags.Hashtag;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.UserService;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.util.MasterService;
import com.example.travelleronline.util.dao.PostDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PostService extends MasterService {

    @Autowired
    private UserService userService;
    @Autowired
    private PostDAO dao;

    public PostCreationDTO createPost(PostCreationDTO dto, int uid) {
        Category c = validatePost(dto);
        User u = getVerifiedUserById(uid);
        Post p = new Post();
        p.setOwner(u);
        p.setDateOfUpload(LocalDateTime.now());
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setLocationLatitude(dto.getLocationLatitude());
        p.setLocationLongitude(dto.getLocationLongitude());
        p.setCategory(c);
        postRepository.save(p);
        dto.setDateOfUpload(p.getDateOfUpload());
        dto.setPostId(p.getPostId());
        return dto;
    }

    public void deletePostById(int pid, int uid) {
        validateDeletionOfPost(pid, uid);
        postRepository.deleteById(pid);
    }

    private void validateDeletionOfPost(int pid, int uid) {
        Post p = getPostById(pid);
        User postOwner = p.getOwner();
        User sessionUser = getVerifiedUserById(uid);
        if (!sessionUser.equals(postOwner)) {
            throw new UnauthorizedException("You must be the post owner to delete this post.");
        }
    }

    public void editPost(int pid, PostCreationDTO dto, int uid) {
        validatePostOwner(pid, uid);
        Category c = validatePost(dto);
        Post existingPost = getPostById(pid);
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
        Category c = categoryRepository.findByName(category);
        if (c != null) {
            return c;
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
        Post p = getPostById(pid);
        User u = getVerifiedUserById(uid);
        p.getTaggedUsers().add(u);
        u.getTaggedInPosts().add(p);
        postRepository.save(p);
        userRepository.save(u);
    }

    public List<PostFilterDTO> filterPosts(String searchBy, String value, String orderBy,
                                           int pageNumber, int rowsNumber) {
        return dao.filterPosts(searchBy, value, orderBy, pageNumber, rowsNumber);
    }

    private Hashtag validateHashtag(String hashtag) {
        Hashtag tag = hashtagRepository.findByName(hashtag);
        if (tag != null) {
            return tag;
        }
        throw new BadRequestException("No such hashtag.");
    }

    public List<PostFilterDTO> getPostsByCategory(String category, int pageNumber, int rowsNumber) {
        Category c = validateCategory(category);
        Pageable page = PageRequest.of(pageNumber, rowsNumber, Sort.by("dateOfUpload"));
        List<Post> posts = postRepository.findAllByCategory(c, page);
        return posts.stream().map(p -> modelMapper.map(p,PostFilterDTO.class)).collect(Collectors.toList());
    }

    public List<PostFilterDTO> showNewsFeed(int uid, int pageNumber, int rowsNumber) {
        return dao.showNewsFeed(uid, pageNumber, rowsNumber);
    }

    public List<PostFilterDTO> getPostsOfUser(int uid, int pageNumber, int rowsNumber) {
        return dao.getPostsOfUser(uid, pageNumber, rowsNumber);
    }

    public LikesDislikesDTO reactTo(int uid, int pid, String reaction) {
        User user = getVerifiedUserById(uid);
        Post post = postRepository.findById(pid)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        PostReaction postReaction = new PostReaction();
        postReaction.setUser(user);
        postReaction.setPost(post);
        if (reaction.equals("like")) {
            postReaction.setLike(true);
        }
        else if (reaction.equals("dislike")) {
            postReaction.setLike(false);
        }
        else {
            throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
        List<PostReaction> reactionsSamePostAndUser = postReactRepo.findAllByUserAndPost(user, post);
        if (reactionsSamePostAndUser.size() == 0) {
            postReactRepo.save(postReaction);
        }
        else {
            PostReaction oldPostReaction = reactionsSamePostAndUser.get(0);
            postReactRepo.delete(oldPostReaction);
            if (oldPostReaction.isLike() != postReaction.isLike()) {
                postReactRepo.save(postReaction);
            }
        }
        LikesDislikesDTO dto = new LikesDislikesDTO();
        int likes = post.getPostReactions().stream()
                .filter(pr -> pr.isLike())
                .collect(Collectors.toList())
                .size();
        dto.setLikes(likes);
        int dislikes = post.getPostReactions().size() - likes;
        dto.setDislikes(dislikes);
        return dto;
    }

    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(int pid, String reaction) {
        Post post = postRepository.findById(pid)
            .orElseThrow(() -> new NotFoundException("Post not found."));
        switch (reaction) {
            case "like" -> {
                return post.getPostReactions().stream()
                        .filter(pr -> pr.isLike())
                        .map(pr -> modelMapper.map(pr.getUser(), UserIdNamesPhotoDTO.class))
                        .collect(Collectors.toList());
            }
            case "dislike" -> {
                return post.getPostReactions().stream()
                        .filter(pr -> !pr.isLike())
                        .map(pr -> modelMapper.map(pr.getUser(), UserIdNamesPhotoDTO.class))
                        .collect(Collectors.toList());
            }
            default -> throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
    }

}