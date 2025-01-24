package com.project.blog.controller;

import com.project.blog.dto.request.comment.CommentReqDto;
import com.project.blog.dto.response.Comment.CommentResDto;
import com.project.blog.entity.User;
import com.project.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/list")
    public ResponseEntity<Page<CommentResDto>> commentList(@PathVariable Long boardId, @PageableDefault(size = 5, sort="id", direction = Sort.Direction.DESC) Pageable pageable){
        Page<CommentResDto> commentList = commentService.getAllComments(pageable, boardId);
        return ResponseEntity.status(HttpStatus.OK).body(commentList);
    }

    @PostMapping("/write")
    public ResponseEntity<CommentResDto> write(@RequestBody CommentReqDto commentReqDto, @PathVariable Long boardId, @AuthenticationPrincipal User user) {
        CommentResDto saveCommentDto = commentService.writeComment(boardId,user,commentReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveCommentDto);
    }

    @PatchMapping("/update/{commentId}")
    public ResponseEntity<CommentResDto> update(@PathVariable Long commentId, @PathVariable String boardId,@RequestBody CommentReqDto commentReqDto) {
        CommentResDto updateCommentDto = commentService.update(commentId,commentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateCommentDto);
    }
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Long> delete(@PathVariable Long commentId, @PathVariable String boardId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
