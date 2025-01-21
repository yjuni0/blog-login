package com.project.blog.entity;

import com.project.blog.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime expiresDate;

    @Builder
    public RefreshToken(String email, String token, LocalDateTime createdDate, LocalDateTime expiresDate) {
        this.email = email;
        this.token = token;
        this.createdDate = createdDate;
        this.expiresDate = expiresDate;
    }

    // 토큰 업데이트 메서드
    public void updateToken(String token) {
        this.token = token;
    }


}
