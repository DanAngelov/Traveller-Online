package com.example.travelleronline.posts;

import com.example.travelleronline.general.exceptions.BadRequestException;
import com.example.travelleronline.posts.dtos.PostFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class PostDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL_TITLE_DATE = "SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "p.location_latitude AS location_latitude, p.location_longitude AS location_longitude " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "WHERE title LIKE ? ORDER BY p.date_of_upload DESC LIMIT ?, ?";

    private static final String SQL_TITLE_LIKES = "SELECT post_id, category, title, user_full_name FROM " +
            "(SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "(pr.is_like * COUNT(*)) AS likes_number " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "LEFT JOIN post_reactions AS pr ON (pr.post_id = p.post_id) " +
            "WHERE title LIKE ? GROUP BY p.post_id, pr.is_like) intermediate_results " +
            "GROUP BY post_id ORDER BY SUM(likes_number) DESC LIMIT ?, ?";

    private static final String SQL_HASHTAG_DATE = "SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "p.location_latitude AS location_latitude, p.location_longitude AS location_longitude " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN post_hashtags AS ph ON (p.post_id = ph.post_id) " +
            "JOIN hashtags AS h ON (ph.hashtag_id = h.hashtag_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "WHERE h.`name` = ? ORDER BY p.date_of_upload DESC LIMIT ?, ?";

    private static final String SQL_HASHTAG_LIKES = "SELECT post_id, category, title, user_full_name FROM " +
            "(SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "(pr.is_like * COUNT(*)) AS likes_number " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN post_hashtags AS ph ON (p.post_id = ph.post_id) " +
            "JOIN hashtags AS h ON (ph.hashtag_id = h.hashtag_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "LEFT JOIN post_reactions AS pr ON (pr.post_id = p.post_id) " +
            "WHERE h.`name` = ? GROUP BY p.post_id, pr.is_like) intermediate_results " +
            "GROUP BY post_id ORDER BY SUM(likes_number) DESC LIMIT ?, ?";

    private static final String SQL_NEWS_FEED = "SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "p.location_latitude AS location_latitude, p.location_longitude AS location_longitude " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "JOIN subscribers AS s ON (u.user_id = s.user_id) " +
            "WHERE s.sub_id = ? " +
            "ORDER BY p.date_of_upload DESC LIMIT ?, ?";

    private static final String SQL_PROFILE_PAGE = "SELECT p.post_id AS post_id, pc.`name` AS category, " +
            "p.title AS title, CONCAT(u.first_name, ' ', u.last_name) AS user_full_name, " +
            "p.location_latitude AS location_latitude, p.location_longitude AS location_longitude " +
            "FROM posts AS p " +
            "JOIN post_categories AS pc ON (p.category_id = pc.category_id) " +
            "JOIN users AS u ON (p.user_id = u.user_id) " +
            "WHERE u.user_id = ? " +
            "ORDER BY p.date_of_upload DESC LIMIT ?, ?";

    List<PostFilterDTO> filterPosts(String searchBy, String searchValue, String orderBy,
                                           int pageNumber, int rowsNumber) {
        String sql = "";
        switch (searchBy) {
            case "title" -> {
                switch (orderBy) {
                    case "date" -> sql = SQL_TITLE_DATE;
                    case "likes" -> sql = SQL_TITLE_LIKES;
                    default -> throw new BadRequestException("Unknown value or parameter \"orderBy\".");
                }
            }
            case "hashtag" -> {
                switch (orderBy) {
                    case "date" -> sql = SQL_HASHTAG_DATE;
                    case "likes" -> sql = SQL_HASHTAG_LIKES;
                    default -> throw new BadRequestException("Unknown value or parameter \"orderBy\".");
                }
            }
            default -> throw new BadRequestException("Unknown value or parameter \"searchBy\".");
        }
        int skipsNumber = pageNumber * rowsNumber;
        return jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setString(1, searchValue);
                        ps.setInt(2, skipsNumber);
                        ps.setInt(3, rowsNumber);
                    }
                }, (rs, rowNum) -> new PostFilterDTO(
                        rs.getInt("post_id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("user_full_name"),
                        rs.getDouble("location_latitude"),
                        rs.getDouble("location_longitude")));
    }


    List<PostFilterDTO> showNewsFeed(int uid, int pageNumber, int rowsNumber) {
        return getPostsForUserID(SQL_NEWS_FEED, uid, pageNumber, rowsNumber);
    }

    List<PostFilterDTO> getPostsOfUser(int uid, int pageNumber, int rowsNumber) {
        return getPostsForUserID(SQL_PROFILE_PAGE, uid, pageNumber, rowsNumber);
    }

    private List<PostFilterDTO> getPostsForUserID(String sql, int uid, int pageNumber, int rowsNumber) {
        int skipsNumber = pageNumber * rowsNumber;
        return jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setInt(1, uid);
                        ps.setInt(2, skipsNumber);
                        ps.setInt(3, rowsNumber);
                    }
                }, (rs, rowNum) -> new PostFilterDTO(
                        rs.getInt("post_id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("user_full_name"),
                        rs.getDouble("location_latitude"),
                        rs.getDouble("location_longitude")));
    }

}