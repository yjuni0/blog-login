package com.project.blog.dto.request;


import com.project.blog.common.Role;
import com.project.blog.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterDto {
    private String email;
    private String password;
    private String checkPassword;
    private String userName;

    @Builder
    public RegisterDto(String email, String password, String checkPassword, String userName) {
        this.email = email;
        this.password = password;
        this.checkPassword = checkPassword;
        this.userName = userName;
    }

    // DTO -> Entity
    public static User ofEntity(RegisterDto dto){
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .userName(dto.getUserName())
                .role(Role.USER)
                .build();
    }
}
