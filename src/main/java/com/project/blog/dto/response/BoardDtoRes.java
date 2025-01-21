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
public class BoardDtoRes {

    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdDate;



    @Builder
    public BoardDtoRes(Long boardId, String title, String content, String writer, LocalDateTime createdDate) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.createdDate = createdDate;
    }

    public static BoardDtoRes fromEntity(Board board) {
        return BoardDtoRes.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getUser().getUsername())
                .createdDate(board.getCreatedDate())
                .build();
    }
}
