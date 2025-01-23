package com.project.blog.service;

import com.project.blog.dto.response.user.UserDto;
import com.project.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    // 전체 유저 조회
    public Page<UserDto> findAllUser(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setId(user.getId());
                    dto.setUserEmail(user.getEmail());
                    dto.setUserName(user.getUserNickName());
                    return dto;
                });
    }
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
