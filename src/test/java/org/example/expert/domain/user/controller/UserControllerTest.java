package org.example.expert.domain.user.controller;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("유저 생성 테스트")
    void generateUserByNickname()throws SQLException {
        long startTime = System.currentTimeMillis();

        Set<String> nicknames = new HashSet<>();
        List<User> users = new ArrayList<>();
        int batchSize = 10000;

        for (int i = 0; i < 1000000; i++) {
            String nickname;
            do {
                nickname = UUID.randomUUID().toString();
            } while (nicknames.contains(nickname));
            nicknames.add(nickname);

            User user = new User("email" + i + "@example.com", nickname, "password", UserRole.USER);
            users.add(user);

            if (users.size() == batchSize) {
                saveUsersBatch(users);
                users.clear();
            }
        }

        if (!users.isEmpty()) {
            saveUsersBatch(users);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken to generate 1,000,000 users: " + duration + " milliseconds");
    }

    private void saveUsersBatch(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (email, nickname, password, user_role, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (User user : users) {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRole().name());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    @Test
    @DisplayName("유저 조회 테스트1")
    void getUsersByNickname() {
        long startTime = System.currentTimeMillis();

        List<User> users = userRepository.findByNickname("4764e350-9917-488b-9376-ffa4f113ce9c");

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("유저 조회 시간: " + duration + "milliseconds");

        users.forEach(user -> System.out.println(user.getNickname()));
    }

    @Test
    @DisplayName("유저 조회 테스트2")
    void getUsersByNicknameWithNoCache() {
        long startTime = System.currentTimeMillis();

        String sql = "SELECT SQL_NO_CACHE * FROM users WHERE nickname = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{"4764e350-9917-488b-9376-ffa4f113ce9c"}, new UserRowMapper());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("유저 조회 시간 (SQL_NO_CACHE): " + duration + " milliseconds");

        users.forEach(user -> System.out.println(user.getNickname()));
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getString("email"),
                    rs.getString("nickname"),
                    rs.getString("password"),
                    UserRole.valueOf(rs.getString("user_role"))
            );
        }
    }
}
