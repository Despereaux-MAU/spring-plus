package org.example.expert.domain.user.controller;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUsersByNickname() {
        Set<String> nicknames = new HashSet<>();
        for (int i = 0; i < 1000000; i++) {
            String nickname;
            do {
                nickname = UUID.randomUUID().toString();
            } while (nicknames.contains(nickname));
            nicknames.add(nickname);

            User user = new User("email" + i + "@example.com", nickname, "password", UserRole.USER);
            userRepository.save(user);
        }
    }
}