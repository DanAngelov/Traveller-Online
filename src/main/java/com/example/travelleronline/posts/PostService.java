package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.general.exceptions.NotFoundException;
import com.example.travelleronline.posts.dtos.PostEditDTO;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import com.example.travelleronline.posts.dtos.PostDTO;
import com.example.travelleronline.reactions.dto.LikesDislikesDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends MasterService {

    private static final int LONGITUDE_MIN_VALUE = -180;
    private static final int LONGITUDE_MAX_VALUE = 180;
    private static final int LATITUDE_MIN_VALUE = -90;
    private static final int LATITUDE_MAX_VALUE = 90;
    private static final int POST_DESCRIPTION_LENGTH_MAX = 500;
    private static final int POST_TITLE_LENGTH_MIN = 3;
    private static final int POST_TITLE_LENGTH_MAX = 100;

    @Autowired
    private PostDAO dao;

    PostCreationDTO createPost(PostCreationDTO dto, int uid) {
        Category category = validatePost(dto);
        User user = getVerifiedUserById(uid);
        Post post = modelMapper.map(dto,Post.class);
        post.setOwner(user);
        post.setDateOfUpload(LocalDateTime.now());
        post.setCategory(category);
        postRepository.save(post);
        dto.setDateOfUpload(post.getDateOfUpload());
        dto.setPostId(post.getPostId());
        return dto;
    }

    PostDTO getPost(int pid) {
        return modelMapper.map(getPostById(pid), PostDTO.class);
    }

    List<PostFilterDTO> getPostsByCategory(String category, int pageNumber, int rowsNumber) {
        Category c = validateCategory(category);
        Pageable page = PageRequest.of(pageNumber, rowsNumber, Sort.by("dateOfUpload"));
        List<Post> posts = postRepository.findAllByCategory(c, page);
        return posts.stream().map(p -> modelMapper.map(p,PostFilterDTO.class)).collect(Collectors.toList());
    }

    void editPost(int pid, PostEditDTO dto, int uid) {
        Post existingPost = validatePostOwner(pid, uid);
        Category category = validatePost(dto);
        existingPost.setCategory(category);
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        postRepository.save(existingPost);
    }

    void deletePostById(int pid, int uid) {
        validatePostOwner(pid, uid);
        postRepository.deleteById(pid);
    }

    void tagUserToPost(int pid, int uid, int sessionUserId) {
        Post post = validatePostOwner(pid,sessionUserId);
        User user = getVerifiedUserById(uid);
        if (user.getTaggedInPosts().contains(post)) {
            user.getTaggedInPosts().remove(post);
        }
        else {
            user.getTaggedInPosts().add(post);
        }
        userRepository.save(user);
    }

    LikesDislikesDTO reactTo(int uid, int pid, String reaction) {
        User user = getVerifiedUserById(uid);
        Post post = getPostById(pid);
        PostReaction postReaction = new PostReaction();
        postReaction.setUser(user);
        postReaction.setPost(post);
        switch (reaction) {
            case "like" -> postReaction.setLike(true);
            case "dislike" -> postReaction.setLike(false);
            default -> throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
        updatePostReaction(user, post, postReaction);
        return getLikesAndDislikes(post);
    }

    List<UserIdNamesPhotoDTO> getUsersWhoReacted(int pid, String reaction) {
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

    List<PostFilterDTO> filterPosts(String searchBy, String searchVaule, String orderBy,
                                    int pageNumber, int rowsNumber) {
        if (searchBy.equals("title")) {
            StringBuilder builder = new StringBuilder();
            searchVaule = searchVaule.replace("_", " ");
            searchVaule = builder.append("%").append(searchVaule).append("%").toString();
        }
        return dao.filterPosts(searchBy, searchVaule, orderBy, pageNumber, rowsNumber);
    }

    List<PostFilterDTO> showNewsFeed(int uid, int pageNumber, int rowsNumber) {
        return dao.showNewsFeed(uid, pageNumber, rowsNumber);
    }

    List<PostFilterDTO> getPostsOfUser(int uid, int pageNumber, int rowsNumber) {
        return dao.getPostsOfUser(uid, pageNumber, rowsNumber);
    }

    private void updatePostReaction(User user, Post post, PostReaction postReaction) {
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
    }

    private LikesDislikesDTO getLikesAndDislikes(Post post) {
        LikesDislikesDTO dto = new LikesDislikesDTO();
        int likes = post.getPostReactions().stream()
                .filter(pr -> pr.isLike()).toList()
                .size();
        dto.setLikes(likes);
        int dislikes = post.getPostReactions().size() - likes;
        dto.setDislikes(dislikes);
        return dto;
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

    private Category validateCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category != null) {
            return category;
        }
        throw new BadRequestException("No such category.");
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Title can not be blank.");
        }
        if (title.length() < POST_TITLE_LENGTH_MIN || title.length() > POST_TITLE_LENGTH_MAX) {
            throw new BadRequestException("Title must be between " + POST_TITLE_LENGTH_MIN +
                    " and " + POST_TITLE_LENGTH_MAX + " letters.");
        }
    }

    private void validateDescription(String desc) {
        if (desc.length() > POST_DESCRIPTION_LENGTH_MAX) {
            throw new BadRequestException("Description must not be over " +
                    POST_DESCRIPTION_LENGTH_MAX + " letters.");
        }
    }

    private void validateLocation(double locationLatitude, double locationLongitude) {
        if (locationLongitude < LONGITUDE_MIN_VALUE || locationLongitude > LONGITUDE_MAX_VALUE ||
            locationLatitude < LATITUDE_MIN_VALUE || locationLatitude > LATITUDE_MAX_VALUE) {
            throw new BadRequestException("Location latitude must be a number between " +
                    LATITUDE_MIN_VALUE + " and " + LATITUDE_MAX_VALUE +
                    " and the longitude between " + LONGITUDE_MIN_VALUE +
                    " and " + LONGITUDE_MAX_VALUE + ".");
        }
    }

}