package com.project.blog.dto.request.comment;

import com.project.blog.dto.response.Comment.CommentResDto;
import com.project.blog.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentReqDto {
    private String content;

    @Builder
    public CommentReqDto(String content) {
        this.content = content;
    }

    public static Comment ofEntity(CommentReqDto commentReqDto) {
        return Comment.builder()
                .content(commentReqDto.getContent())
                .build();
    }
}
