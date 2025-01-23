package com.project.blog.dto.response.user;

import com.project.blog.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private String userName;

    @Builder
    public UserResponseDto(String email, String userName) {
        this.email = email;
        this.userName = userName;
    }

    //Entity -> Dto
    public static UserResponseDto fromEntity(User user){
        return UserResponseDto.builder()
                .email(user.getEmail())
                .userName(user.getUserNickName())
                .build();
    }

}
