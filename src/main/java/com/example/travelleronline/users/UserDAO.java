package com.example.travelleronline.users;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String SQL_GET_DELETED_USERS_PHOTOS = "SELECT user_photo_uri AS uri FROM users " +
            "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";

    public static final String SQL_SOFTLY_DELETE_USERS = "UPDATE users " +
            "SET first_name AS ' ', last_name AS ' ', email AS ' ', phone AS ' ', " +
            "date_of_birth AS CURDATE(), gender AS 'n', user_photo_uri AS ' ' " +
            "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";

    @SneakyThrows
    public void deleteUsersNotLoggedInSoon() {
        List<String> uriForDelete = jdbcTemplate
                .query(SQL_GET_DELETED_USERS_PHOTOS, (rs, rowNum) -> rs.getString("uri"));
        for (String uri : uriForDelete) {
            Files.delete(Path.of(uri));
        }
        jdbcTemplate.update(SQL_SOFTLY_DELETE_USERS);
    }

}