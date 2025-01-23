package com.project.blog.service;

import com.project.blog.common.exception.ResourceNotFoundException;
import com.project.blog.dto.request.comment.CommentReqDto;
import com.project.blog.dto.response.Comment.CommentResDto;
import com.project.blog.entity.Board;
import com.project.blog.entity.Comment;
import com.project.blog.entity.User;
import com.project.blog.repository.BoardRepository;
import com.project.blog.repository.CommentRepository;
import com.project.blog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 댓글 모두 조회
    public Page<CommentResDto> getAllComments(Pageable pageable, Long boardId) {
        Page<Comment> comments = commentRepository.findAllWithUserAndBoard(pageable,boardId);
        List<CommentResDto> commentList = comments.getContent().stream().map(CommentResDto::fromEntity).toList();
        return new PageImpl<>(commentList, comments.getPageable(), comments.getTotalElements());
    }

    //댓글 작성
    public CommentResDto writeComment(Long boardId, User user, CommentReqDto commentReqDto) {
        Board board = boardRepository.findById(boardId).orElseThrow(()->new ResourceNotFoundException("board","boardId",String.valueOf(boardId)));
        User commentWriter = userRepository.findById(user.getId()).orElseThrow(()->new ResourceNotFoundException("user","userId",String.valueOf(user.getId())));

        Comment comment = CommentReqDto.ofEntity(commentReqDto);
        comment.setBoard(board);
        comment.setUser(commentWriter);
        Comment savedComment = commentRepository.save(comment);
        return CommentResDto.fromEntity(savedComment);
    }

    // 댓글 수정
    public CommentResDto update(Long commentId, CommentReqDto commentReqDto) {
        Comment comment = commentRepository.findByWithUserAndBoard(commentId).orElseThrow(()->new ResourceNotFoundException("Comment","Comment Id", String.valueOf(commentId)));
        comment.update(commentReqDto.getContent());
        return CommentResDto.fromEntity(comment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
