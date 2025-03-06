package com.danahub.zipitda.user.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import com.danahub.zipitda.user.domain.AuthProvider;
import com.danahub.zipitda.user.domain.User;
import com.danahub.zipitda.user.dto.UserRegisterRequestDto;
import com.danahub.zipitda.user.dto.UserResponseDto;
import com.danahub.zipitda.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 해싱

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 존재하지 않으면 사용 가능
    }

    @Transactional
    public UserResponseDto registerUser(UserRegisterRequestDto request) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new ZipitdaException(ErrorType.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.nickname())) {
            throw new ZipitdaException(ErrorType.DUPLICATE_USERNAME);
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(request.password());

        // 회원 정보 저장
        User user = User.builder()
                .email(request.email())
                .password(hashedPassword)
                .nickname(request.nickname())
                .role("USER")
                .isActive("INACTIVE") // 이메일 인증 후 활성화
                .provider(AuthProvider.LOCAL)
                .build();

        userRepository.save(user);

        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getIsActive(),
                user.getProfileImage()
        );
    }
}