package com.project.blog.dto.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateDto {
    private String password;
    private String passwordCheck;
    private String username;

    @Builder
    public UserUpdateDto(String password, String passwordCheck, String username) {
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.username = username;
    }

}