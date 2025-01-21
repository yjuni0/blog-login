package com.project.blog.controller;

import com.project.blog.dto.request.BoardUpdateDto;
import com.project.blog.dto.request.BoardWriteDto;
import com.project.blog.dto.request.SearchDto;
import com.project.blog.dto.response.BoardDetailDto;
import com.project.blog.dto.response.BoardDtoRes;
import com.project.blog.entity.User;
import com.project.blog.service.BoardService;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 메인 페이지 게시글 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Page<BoardDtoRes>> boardList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BoardDtoRes> listDto = boardService.getAllBoards(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listDto);
    }
    //검색 조회
    @GetMapping("/search")
    public ResponseEntity<Page<BoardDtoRes>> search(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam String title,
                                                    @RequestParam String content,
                                                    @RequestParam String writer){
        SearchDto searchDto = SearchDto.createSearchData(title,content,writer);
        Page<BoardDtoRes> listDto = boardService.search(searchDto, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listDto);
    }

    @PostMapping("/write")
    public ResponseEntity<BoardDtoRes> write(@RequestBody BoardWriteDto writeDto, @AuthenticationPrincipal User user){
        BoardDtoRes saveBoardDto = boardService.write(writeDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveBoardDto);
    }

    // 게시글 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDto> detail(@PathVariable("boardId") Long boardId){
        BoardDetailDto detailDto = boardService.detail(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(detailDto);
    }

    @PutMapping("/{boardId}/update")
    public ResponseEntity<BoardDetailDto> update(@PathVariable("boardId") Long boardId, @RequestBody BoardUpdateDto updateDto){
        BoardDetailDto updateBoardDto = boardService.update(updateDto,boardId);
        return ResponseEntity.status(HttpStatus.OK).body(updateBoardDto);
    }

    @DeleteMapping("/{boardId}/delete")
    public ResponseEntity<BoardDetailDto> delete(@PathVariable("boardId") Long boardId){
        boardService.delete(boardId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
