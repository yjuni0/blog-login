package com.project.blog.dto.response.user;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
public class UserTokenDto {
    private String email;
    private String accessToken;


    @Builder
    public UserTokenDto(String accessToken, String email) {
        this.accessToken = accessToken;
        this.email = email;
    }

    //Entity -> Dto
    public static UserTokenDto fromEntity(UserDetails user, String accessToken) {
        return UserTokenDto.builder()
                .email(user.getUsername())
                .accessToken(accessToken)
                .build();
    }
}
