package com.project.blog.controller;


import com.project.blog.dto.request.file.FileReqDto;
import com.project.blog.dto.response.file.FileResDto;
import com.project.blog.entity.Board;
import com.project.blog.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/board/{boardId}/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResDto> upload(@PathVariable Long boardId, @RequestParam("file") MultipartFile file)throws IOException {
        FileResDto saveFileResDto = fileService.upload(boardId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveFileResDto);
    }

    // 파일 조회 (단일 파일)
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResDto> get(@PathVariable Long boardId, @PathVariable Long fileId) {
        FileResDto getFile = fileService.getFile(fileId);
        return ResponseEntity.status(HttpStatus.OK).body(getFile);
    }

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId, @PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 파일이 성공적으로 삭제되면 204 상태 반환
    }
}