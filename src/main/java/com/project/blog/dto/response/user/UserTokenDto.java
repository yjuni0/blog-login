package com.project.blog.dto.response.user;


import com.project.blog.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor
public class UserTokenDto {

    private String userName;
    private String accessToken;


    @Builder
    public UserTokenDto(String accessToken,String userName) {
        this.accessToken = accessToken;
        this.userName = userName;

    }

    //Entity -> Dto
    public static UserTokenDto fromEntity(User user, String accessToken) {
        return UserTokenDto.builder()
                .userName(user.getUserNickName())
                .accessToken(accessToken)
                .build();
    }
}
