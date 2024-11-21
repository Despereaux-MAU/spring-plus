package org.example.expert.domain.user.controller;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void getUsersByNickname() throws SQLException {
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
}
