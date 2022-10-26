package com.example.travelleronline.util;

import com.example.travelleronline.categories.CategoryRepository;
import com.example.travelleronline.comments.CommentRepository;
import com.example.travelleronline.exceptions.BadRequestException;
import com.example.travelleronline.exceptions.NotFoundException;
import com.example.travelleronline.hashtags.HashtagRepository;
import com.example.travelleronline.media.PostImageRepository;
import com.example.travelleronline.posts.PostRepository;
import com.example.travelleronline.reactions.toComment.CommentReactionRepository;
import com.example.travelleronline.reactions.toPost.PostReactionRepository;
import com.example.travelleronline.users.User;
import com.example.travelleronline.users.UserRepository;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class MasterService {
    @Autowired
    protected ModelMapper modelMapper;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PostImageRepository postImageRepository;
    @Autowired
    protected HashtagRepository hashtagRepository;
    @Autowired
    protected PostReactionRepository postReactRepo;
    @Autowired
    protected CommentReactionRepository commentReactRepo;

    protected User getVerifiedUserById(int uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found."));
        if (!user.isVerified()) {
            throw new BadRequestException("The user is not verified.");
        }
        return user;
    }

//    protected Post getPostById(int pid) {
//        return postRepository.findById(pid)
//                .orElseThrow(() -> new NotFoundException("Post not found."));
//    } // TODO use everywhere

    // Cron Job

    @Scheduled(cron = "0 0 0 */10 * *")
    public void deleteUsersNotVerified() {
        List<User> usersNotVerified = userRepository.findAllByIsVerified(false).stream()
                .filter(user -> !user.getEmail().isBlank()) // to skip softly deleted users
                .filter(user ->
                        Period.between(user.getCreatedAt().toLocalDate(), LocalDate.now()).getDays() >= 10)
                .collect(Collectors.toList());
        userRepository.deleteAll(usersNotVerified);
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0 */180 * *")
    public void deleteUsersNotLoggedInSoon() {
        String sql1 = "SELECT user_photo_uri AS uri FROM users " +
                "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";
        List<String> uriForDelete = jdbcTemplate
                .query(sql1, (rs, rowNum) -> rs.getString("uri"));
        for (String uri : uriForDelete) {
            Files.delete(Path.of(uri));
        }
        String sql2 = "UPDATE users " +
                "SET first_name AS ' ', last_name AS ' ', email AS ' ', phone AS ' ', " +
                "date_of_birth AS CURDATE(), gender AS 'n', user_photo_uri AS ' ' " +
                "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";
        jdbcTemplate.update(sql2);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void completeDeleteOfUsers() {
        // HERE - statistics report (if needed)
        List<User> usersSoftlyDeleted = userRepository.findAllByEmail(" ");
        userRepository.deleteAll(usersSoftlyDeleted);
    }

}