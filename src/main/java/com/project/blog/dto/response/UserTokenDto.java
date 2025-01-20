package com.project.blog.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
public class UserTokenDto {
    private String email;
    private String accessToken;
    private String refreshToken;

    @Builder
    public UserTokenDto(String accessToken, String refreshToken, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
    }

    //Entity -> Dto
    public static UserTokenDto fromEntity(UserDetails user, String accessToken, String refreshToken) {
        return UserTokenDto.builder()
                .email(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
