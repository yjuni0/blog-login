package com.project.blog.dto.response.user;

import com.project.blog.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class UserDto {
    private Long id;
    private String userEmail;
    private String userName;

    @Builder
    public UserDto(Long id, String userEmail, String userName) {
        this.id = id;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userEmail(user.getEmail())
                .userName(user.getUserNickName())
                .build();
    }
}
