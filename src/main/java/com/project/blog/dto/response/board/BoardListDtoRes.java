package com.project.blog.dto.response.board;

import com.project.blog.entity.Board;
import com.project.blog.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BoardListDtoRes {

    private Long boardId;
    private String title;
    private String content;
    private String writer;
    private int viewCount;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime createdDate;


    @Builder
    public BoardListDtoRes(Long boardId, String title, String content, String writer, int viewCount, String fileName, String filePath, String fileType, LocalDateTime createdDate) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.viewCount = viewCount;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.createdDate = createdDate;
    }

    public static BoardListDtoRes fromEntity(Board board) {
        FileEntity file = board.getBackgroundImage(); // 파일 정보
        return BoardListDtoRes.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .fileName(file != null ? file.getFileName() : null)
                .filePath(file != null ? file.getFilePath() : null)  // 파일 경로
                .fileType(file != null ? file.getFileType() : null)
                .writer(board.getUser().getUserNickName())
                .viewCount(board.getViewCount())
                .createdDate(board.getCreatedDate())
                .build();
    }
}
