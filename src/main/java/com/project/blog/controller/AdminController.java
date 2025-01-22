package com.project.blog.controller;


import com.project.blog.common.exception.UserException;
import com.project.blog.dto.request.BoardWriteDto;
import com.project.blog.dto.request.SearchDto;
import com.project.blog.dto.response.BoardDetailDto;
import com.project.blog.dto.response.BoardDtoRes;
import com.project.blog.dto.response.UserDto;
import com.project.blog.entity.User;
import com.project.blog.repository.UserRepository;
import com.project.blog.service.AdminService;
import com.project.blog.service.BoardService;
import com.project.blog.service.UserService;
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

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final BoardService boardService;
    private final UserRepository userRepository;
    // 관리자 페이지 구현
    // 관리자 유저 정보 검색 , 유저 전체 조회, 유저 상세 조회, 유저 수정,삭제
    // 게시글 전체 조회, 게시글 상세 조회, 게시글 삭제

    // < -------- Board --------- >

    // 메인 페이지 게시글 전체 조회
    @GetMapping("/board/list")
    public ResponseEntity<Page<BoardDtoRes>> boardList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BoardDtoRes> listDto = boardService.getAllBoards(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listDto);
    }
    //검색 조회
    @GetMapping("/board/search")
    public ResponseEntity<Page<BoardDtoRes>> search(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam String title,
                                                    @RequestParam String content,
                                                    @RequestParam String writer){
        SearchDto searchDto = SearchDto.createSearchData(title,content,writer);
        Page<BoardDtoRes> listDto = boardService.search(searchDto, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listDto);
    }

    @PostMapping("/board/write")
    public ResponseEntity<BoardDtoRes> write(@RequestBody BoardWriteDto writeDto, @AuthenticationPrincipal User user){
        BoardDtoRes saveBoardDto = boardService.write(writeDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveBoardDto);
    }

    // 게시글 상세 조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardDetailDto> detail(@PathVariable("boardId") Long boardId){
        BoardDetailDto detailDto = boardService.detail(boardId);
        return ResponseEntity.status(HttpStatus.OK).body(detailDto);
    }

    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<BoardDetailDto> deleteBoard(@PathVariable("boardId") Long boardId){
        boardService.delete(boardId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // < --------- User ----------- >
    // 유저 전체 조회
    @GetMapping("/user/list")
    public ResponseEntity<Page<UserDto>> userList(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        Page<UserDto> list = adminService.findAllUser(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
    // 유저 상세조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> userDetail(@PathVariable("userId") Long userId){
        UserDto user = UserDto.fromEntity(userRepository.findById(userId).orElseThrow(()->new UserException("user not found:"+userId)));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // 유저 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("userId") Long userId){
        adminService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

