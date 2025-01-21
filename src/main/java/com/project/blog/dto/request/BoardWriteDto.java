package com.project.blog.dto.request;

import com.project.blog.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardWriteDto {
    private String title;
    private String content;

    @Builder
    public BoardWriteDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Board ofEntity(BoardWriteDto writeDto) {
        return Board.builder()
                .title(writeDto.title)
                .content(writeDto.content)
                .build();
    }
}
