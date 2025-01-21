package com.project.blog.service;

import com.project.blog.common.exception.ResourceNotFoundException;
import com.project.blog.dto.request.BoardUpdateDto;
import com.project.blog.dto.request.BoardWriteDto;
import com.project.blog.dto.request.SearchDto;
import com.project.blog.dto.response.BoardDetailDto;
import com.project.blog.dto.response.BoardDtoRes;
import com.project.blog.entity.Board;
import com.project.blog.entity.User;
import com.project.blog.repository.BoardRepository;
import com.project.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 첫 페이징 화면 전체 게시글 로드 페이징 처리
    public Page<BoardDtoRes> getAllBoards(Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        List<BoardDtoRes> result = boards.getContent().stream()
                .map(BoardDtoRes::fromEntity)
                .toList();
        return new PageImpl<>(result, pageable, boards.getTotalElements());
    }
    // 게시글 검색 기능
    public Page<BoardDtoRes> search(SearchDto searchDto, Pageable pageable) {
        Page<Board> result = null;
        if (!searchDto.getTitle().isEmpty()){
            result = boardRepository.findByTitleContaining(searchDto.getTitle(), pageable);
        }else if (!searchDto.getContent().isEmpty()){
            result = boardRepository.findByContentContaining(searchDto.getContent(), pageable);
        }else if(!searchDto.getWriter().isEmpty()){
            result = boardRepository.findByWriter(searchDto.getWriter(), pageable);
        }else {
            // 조건이 없을 경우 빈 페이지 반환
            return Page.empty(pageable);
        }
        List<BoardDtoRes> list = result.getContent().stream()
                .map(BoardDtoRes::fromEntity)
                .toList();
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

    // 게시글 상세 보기
    public BoardDetailDto detail(Long boardId) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(
                ()-> new ResourceNotFoundException("board","board Id",String.valueOf(boardId)));
        return BoardDetailDto.fromEntity(findBoard);
    }
    // 게시글 등록
    public BoardDtoRes write(BoardWriteDto writeDto, User user){
        Board board = BoardWriteDto.ofEntity(writeDto);
        User writer =  userRepository.findByEmail(user.getUsername()).orElseThrow(()->new ResourceNotFoundException("User","UserName", user.getUsername()));
        board.setMappingMember(writer);
        Board savedBoard = boardRepository.save(board);
        return BoardDtoRes.fromEntity(savedBoard);
    }

    // 게시글 수정
    public BoardDetailDto update(BoardUpdateDto updateDto, Long boardId){
        Board updateBoard = boardRepository.findById(boardId).orElseThrow(
                ()->new ResourceNotFoundException("board","board Id",String.valueOf(boardId))
        );
        updateBoard.update(updateDto.getTitle(), updateDto.getContent());
        return BoardDetailDto.fromEntity(updateBoard);
    }

    // 게시글 삭제
    public void delete(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            log.warn("Attempted to delete a non-existent board with ID {}", boardId);
            throw new ResourceNotFoundException("board", "board Id", String.valueOf(boardId));
        }
        boardRepository.deleteById(boardId);
    }


}
