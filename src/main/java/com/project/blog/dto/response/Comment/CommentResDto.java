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
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String commentWriteName;

    @Builder
    public CommentResDto(Long commentId, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, String commentWriteName) {
        this.commentId = commentId;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.commentWriteName = commentWriteName;
    }

    public static CommentResDto fromEntity(Comment comment) {
        return CommentResDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .commentWriteName(comment.getUser().getUserNickName())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .build();
    }
}
