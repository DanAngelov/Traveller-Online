package com.example.travelleronline.util.dao;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class UserDAO {

    public static final String SQL_DELETE_1 = "SELECT user_photo_uri AS uri FROM users " +
            "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";
    public static final String SQL_DELETE_2 = "UPDATE users " +
            "SET first_name AS ' ', last_name AS ' ', email AS ' ', phone AS ' ', " +
            "date_of_birth AS CURDATE(), gender AS 'n', user_photo_uri AS ' ' " +
            "WHERE TIMESTAMPDIFF(DAY,NOW(),last_login_at) > 180";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SneakyThrows
    public void deleteUsersNotLoggedInSoon() {
        List<String> uriForDelete = jdbcTemplate
                .query(SQL_DELETE_1, (rs, rowNum) -> rs.getString("uri"));
        for (String uri : uriForDelete) {
            Files.delete(Path.of(uri));
        }
        jdbcTemplate.update(SQL_DELETE_2);
    }

}