package com.project.blog.repository;

import com.project.blog.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import com.project.blog.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 사용자로 리프레시 토큰 찾기
    Optional<RefreshToken> findByUser(User user);

    // 토큰 문자열로 리프레시 토큰 찾기
    Optional<RefreshToken> findByToken(String token);
    // 사용자 ID로 리프레시 토큰 삭제
    void deleteByUser_Id(Long userId);

    // 특정 토큰 값으로 삭제
    void deleteByToken(String token);
}