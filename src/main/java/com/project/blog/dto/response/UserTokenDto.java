package com.project.blog.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
public class UserTokenDto {
    private String email;
    private String token;

    @Builder
    public UserTokenDto(String token, String email) {
        this.token = token;
        this.email = email;
    }

    //Entity -> Dto
    public static UserTokenDto fromEntity(UserDetails user,String token ){
        return UserTokenDto.builder()
                .email(user.getUsername())
                .token(token)
                .build();

    }
}
