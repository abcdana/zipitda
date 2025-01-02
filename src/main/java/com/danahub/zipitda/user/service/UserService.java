package com.danahub.zipitda.user.service;

import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 존재하지 않으면 사용 가능
    }
}