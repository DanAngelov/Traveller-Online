package com.example.travelleronline.posts;

import com.example.travelleronline.categories.Category;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.exceptions.UnauthorizedException;
import com.example.travelleronline.hashtags.Hashtag;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import com.example.travelleronline.reactions.LikesDislikesDTO;
import com.example.travelleronline.reactions.toPost.PostReaction;
import com.example.travelleronline.posts.dtos.PostCreationDTO;
import com.example.travelleronline.posts.dtos.PostWithoutOwnerDTO;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.UserService;
import com.example.travelleronline.users.dtos.UserIdNamesPhotoDTO;
import com.example.travelleronline.util.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PostService extends MasterService {

    @Autowired
    UserService userService;

    public PostCreationDTO createPost(PostCreationDTO dto, int uid) {
        Category c = validatePost(dto);
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
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

    public List<PostWithoutOwnerDTO> getPostsOfUser(int uid) {
        User u = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found."));
        return u.getPosts().stream().map(p -> modelMapper.map(p, PostWithoutOwnerDTO.class)).collect(Collectors.toList());
    }

    public void deletePostById(int pid, int uid) {
        validateDeletionOfPost(pid, uid);
        postRepository.deleteById(pid);
    }

    private void validateDeletionOfPost(int pid, int uid) {
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        User postOwner = p.getOwner();
        User sessionUser = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found."));
        if (!sessionUser.equals(postOwner)) {
            throw new UnauthorizedException("You must be post owner to delete this post.");
        }
    }

    public void editPost(int pid, PostCreationDTO dto, int uid) {
        validatePostOwner(pid, uid);
        Category c = validatePost(dto);
        Post existingPost = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
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

//    public List<PostFilterDTO> filterPosts(String searchBy, String value, String orderBy,
//                                           int pageNumber, int rowsNumber) {
//        String sqlTitleDate = "SELECT p.post_id AS post_id, pc.`name` AS category, p.title AS title, " +
//                "CONCAT(u.first_name, ' ', u.last_name) AS user_full_name " +
//                "FROM posts AS p " +
//                "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
//                "JOIN users AS u ON (p.user_id = u.user_id) " +
//                "WHERE title LIKE ? ORDER BY p.date_of_upload DESC LIMIT ?, ?";
//        String sqlTitleLikes = "SELECT p.post_id AS post_id, pc.`name` AS category, " +
//                "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name " +
//                "FROM posts AS p " +
//                "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
//                "JOIN users AS u ON (p.user_id = u.user_id) " +
//                "LEFT JOIN post_reactions AS pr ON (pr.post_id = p.post_id) " +
//                "WHERE (pr.is_like = '1' OR pr.is_like IS NULL) AND title LIKE ? " +
//                "GROUP BY p.post_id ORDER BY COUNT(*) DESC LIMIT ?, ?";
//        String sqlHashtagDate = "SELECT p.post_id AS post_id, pc.`name` AS category, p.title AS title, " +
//                "CONCAT(u.first_name, ' ', u.last_name) AS user_full_name " +
//                "FROM posts AS p " +
//                "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
//                "JOIN post_hashtags AS ph ON (p.post_id = ph.post_id) " +
//                "JOIN hashtags AS h ON (ph.hashtag_id = h.hashtag_id) " +
//                "JOIN users AS u ON (p.user_id = u.user_id) " +
//                "WHERE h.`name` LIKE ? ORDER BY p.date_of_upload DESC LIMIT ?, ?";
//        String sqlHashtagLikes = "SELECT p.post_id AS post_id, pc.`name` AS category, p.title AS title, " +
//                "CONCAT(u.first_name, ' ', u.last_name) AS user_full_name " +
//                "FROM posts AS p " +
//                "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
//                "JOIN post_hashtags AS ph ON (p.post_id = ph.post_id) " +
//                "JOIN hashtags AS h ON (ph.hashtag_id = h.hashtag_id) " +
//                "JOIN users AS u ON (p.user_id = u.user_id) " +
//                "LEFT JOIN post_reactions AS pr ON (pr.post_id = p.post_id) " +
//                "WHERE (pr.is_like = '1' OR pr.is_like IS NULL) AND h.`name` LIKE ? " +
//                "GROUP BY p.post_id ORDER BY COUNT(*) DESC LIMIT ?, ?";
//        String sql = "";
//        if (searchBy.equals("title")) { // TODO ? how to be refactored
//            if (orderBy.equals("date")) {
//                sql = sqlTitleDate;
//            }
//            else if (orderBy.equals("likes")) {
//                sql = sqlTitleLikes;
//            }
//            else {
//                throw new BadRequestException("Unknown value or parameter \"orderBy\".");
//            }
//        }
//        else if (searchBy.equals("hashtag")) {
//            if (orderBy.equals("date")) {
//                sql = sqlHashtagDate;
//            }
//            else if (orderBy.equals("likes")) {
//                sql = sqlHashtagLikes;
//            }
//            else {
//                throw new BadRequestException("Unknown value or parameter \"orderBy\".");
//            }
//        }
//        else {
//            throw new BadRequestException("Unknown value or parameter \"searchBy\".");
//        }
//        StringBuilder builder = new StringBuilder();
//        value = builder.append("%").append(value).append("%").toString();
//        int offset = pageNumber * rowsNumber;
//        return jdbcTemplate.query(
//                sql, value, pageNumber, rowsNumber,
//                (rs, rowsNumber) -> new PostFilterDTO(
//                        rs.getInt("postId"),
//                        rs.getString("category"),
//                        rs.getString("title"),
//                        rs.getString("userFullName"));
//    }

    public void addHashtagToPost(int pid, String hashtag, int uid) {
        validatePostOwner(pid, uid);
        Post p = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        for (Hashtag g : p.getPostHashtags()) {
            if(g.getName().equals(hashtag)) {
                throw new BadRequestException("Hashtag already included in post");
            }
        }
        Hashtag tag = hashtagRepository.findByName(hashtag);
        if (tag == null) {
            tag = new Hashtag();
            tag.setName(hashtag);
            hashtagRepository.save(tag);
        }
        p.getPostHashtags().add(tag);
        postRepository.save(p);
    }

    private void validatePostOwner(int pid, int uid) {
        Post post = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("Post not found."));
        User sessionUser = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User not found"));
        if(!sessionUser.equals(post.getOwner())) {
            throw new UnauthorizedException("You must be post owner to add hashtags to the post.");
        }
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


//    public List<PostDTO> showNewsFeed(int uid, int daysMin, int daysMax) {
//        List<PostDTO> postsInNewsFeed = new ArrayList<>();
//        List<User> subscriptions = getVerifiedUserById(uid).getSubscriptions();
//        for (User u : subscriptions) {
//            Iterator<Post> it = u.getPosts().listIterator();
//            while (it.hasNext()) {
//                Post post = it.next();
//                long days = getDaysTillNow(post);
//                if (days >= daysMin && days <= daysMax) {
//                    postsInNewsFeed.add(modelMapper.map(post, PostDTO.class));
//                }
//            }
//        }
//        Collections.sort(postsInNewsFeed,
//                (p1, p2) -> p2.getDateOfUpload().compareTo(p1.getDateOfUpload()));
//        return postsInNewsFeed;
//    public List<PostWithoutOwnerDTO> getPostsByHashtag(String hashtag) {
//    }

//    public List<PostDTO> showPostsOfUser(int uid, int daysMin, int daysMax, String orderBy) {
//        List<Post> posts = getVerifiedUserById(uid).getPosts().stream()
//                .filter(post -> (getDaysTillNow(post) >= daysMin && getDaysTillNow(post) <= daysMax))
//                .collect(Collectors.toList());
//        if (orderBy.equals("date")) {
//            Collections.sort(posts,
//                    (p1, p2) -> p2.getDateOfUpload().compareTo(p1.getDateOfUpload()));
//        }
//        else if (orderBy.equals("likes")) {
//            // TODO !!!
//        }
//        else {
//            throw new BadRequestException("Wrong request parameters.");
//        }
//        return posts.stream()
//                .map(post -> modelMapper.map(post, PostDTO.class))
//                .collect(Collectors.toList());
//    }

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
        dto.setLikes(post.getPostReactions().stream()
                .filter(pr -> pr.isLike())
                .collect(Collectors.toList())
                .size());
        dto.setDislikes(post.getPostReactions().stream()
                .filter(pr -> !pr.isLike())
                .collect(Collectors.toList())
                .size()); // TODO ? can be refactored or not
        return dto;
    }

    public List<UserIdNamesPhotoDTO> getUsersWhoReacted(int pid, String reaction) {
        Post post = postRepository.findById(pid)
            .orElseThrow(() -> new NotFoundException("Post not found."));
        if (reaction.equals("like")) {
            return post.getPostReactions().stream()
                    .filter(pr -> pr.isLike())
                    .map(pr -> modelMapper.map(pr.getUser(), UserIdNamesPhotoDTO.class))
                    .collect(Collectors.toList());
        }
        else if (reaction.equals("dislike")) {
            return post.getPostReactions().stream()
                    .filter(pr -> !pr.isLike())
                    .map(pr -> modelMapper.map(pr.getUser(), UserIdNamesPhotoDTO.class))
                    .collect(Collectors.toList());
        }
        else {
            throw new BadRequestException("Unknown value for parameter \"reaction\".");
        }
    }

    private long getDaysTillNow(Post post) {
        return DAYS.between(post.getDateOfUpload().toLocalDate(), LocalDate.now());
    }

}