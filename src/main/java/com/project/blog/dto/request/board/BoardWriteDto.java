package com.project.blog.dto.request.board;

import com.project.blog.entity.Board;
import com.project.blog.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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
