package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.general.exceptions.UnauthorizedException;
import com.example.travelleronline.posts.dtos.PostEditDTO;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.general.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends MasterService {

    public static final int LONGITUDE_MIN_VALUE = -180;
    public static final int LONGITUDE_MAX_VALUE = 180;
    public static final int LATITUDE_MIN_VALUE = -90;
    public static final int LATITUDE_MAX_VALUE = 90;
    public static final int POST_DESCRIPTION_LENGTH_MAX = 500;
    public static final int POST_TITLE_LENGTH_MIN = 3;
    public static final int POST_TITLE_LENGTH_MAX = 100;

    @Autowired
    private PostDAO dao;

    public PostCreationDTO createPost(PostCreationDTO dto, int uid) {
        Category c = validatePost(dto);
        User u = getVerifiedUserById(uid);
        Post p = modelMapper.map(dto,Post.class);
        p.setOwner(u);
        p.setDateOfUpload(LocalDateTime.now());
        p.setCategory(c);
        postRepository.save(p);
        dto.setDateOfUpload(p.getDateOfUpload());
        dto.setPostId(p.getPostId());
        return dto;
    }

    public PostDTO getAPostById(int pid) {
        return modelMapper.map(getPostById(pid), PostDTO.class);
    }

    public void deletePostById(int pid, int uid) {
        validateDeletionOfPost(pid, uid);
        postRepository.deleteById(pid);
    }

    private void validateDeletionOfPost(int pid, int uid) {
        Post p = getPostById(pid);
        User postOwner = p.getOwner();
        if (postOwner.getUserId() != uid) {
            throw new UnauthorizedException("You must be the post owner to delete this post.");
        }
    }

    public void editPost(int pid, PostEditDTO dto, int uid) {
        Post existingPost = validatePostOwner(pid, uid);
        Category c = validatePost(dto);
        existingPost.setCategory(c);
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        postRepository.save(existingPost);
    }

    private Category validatePost(PostCreationDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateLocation(dto.getLocationLatitude(), dto.getLocationLongitude());
        return validateCategory(dto.getCategory());
    }

    private Category validatePost(PostEditDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        return validateCategory(dto.getCategory());
    }

    private void validateLocation(double locationLatitude, double locationLongitude) {
        if (locationLongitude < LONGITUDE_MIN_VALUE || locationLongitude > LONGITUDE_MAX_VALUE ||
            locationLatitude < LATITUDE_MIN_VALUE || locationLatitude > LATITUDE_MAX_VALUE) {
            throw new BadRequestException("Location latitude must be a number between " + LATITUDE_MIN_VALUE + " and " + LATITUDE_MAX_VALUE +
                    " and the longitude between " + LONGITUDE_MIN_VALUE + " and " + LONGITUDE_MAX_VALUE + ".");
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
        if (desc.length() > POST_DESCRIPTION_LENGTH_MAX) {
            throw new BadRequestException("Description must not be over " + POST_DESCRIPTION_LENGTH_MAX + " letters.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.equals("null")) {
            throw new BadRequestException("Title can not be null.");
        }
        if (title.length() < POST_TITLE_LENGTH_MIN || title.isBlank() || title.length() > POST_TITLE_LENGTH_MAX) {
            throw new BadRequestException("Title must be between " + POST_TITLE_LENGTH_MIN + " and " + POST_TITLE_LENGTH_MAX + " letters");
        }
    }

    @Transactional
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