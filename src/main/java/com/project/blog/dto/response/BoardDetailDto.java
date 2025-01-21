package com.project.blog.dto.response;

import com.project.blog.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardDetailDto {
    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public BoardDetailDto(Long boardId, String title, String content, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static BoardDetailDto fromEntity(Board board) {
        return BoardDetailDto.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getUser().getUsername())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();
    }
}
