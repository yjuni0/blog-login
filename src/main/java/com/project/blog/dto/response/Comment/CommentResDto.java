package com.project.blog.dto.response.Comment;

import com.project.blog.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResDto {
    private Long commentId;
    private String content;
    private Long boardId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String commentWriterName;

    @Builder
    public CommentResDto(Long commentId, String content, Long boardId, LocalDateTime createdDate, LocalDateTime modifiedDate, String commentWriterName) {
        this.commentId = commentId;
        this.content = content;
        this.boardId = boardId;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.commentWriterName = commentWriterName;
    }

    public static CommentResDto fromEntity(Comment comment) {
        return CommentResDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .boardId(comment.getBoard().getId())
                .commentWriterName(comment.getUser().getUserNickName())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .build();
    }
}
