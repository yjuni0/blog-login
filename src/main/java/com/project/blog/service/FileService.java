package com.project.blog.service;

import com.project.blog.common.exception.ResourceNotFoundException;
import com.project.blog.dto.response.file.FileResDto;
import com.project.blog.entity.Board;
import com.project.blog.entity.FileEntity;
import com.project.blog.repository.BoardRepository;
import com.project.blog.repository.FileEntityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileEntityRepository fileRepository;
    private final BoardRepository boardRepository;

    @Value("${project.folderPath}")
    private String FOLDER_PATH;

    public FileResDto upload(Long boardId, MultipartFile file) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "boardId", String.valueOf(boardId)));

        // 파일 이름과 경로 설정
        String fileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString().replaceAll("-", "");
        String filePath = FOLDER_PATH + File.separator + randomId;  // 경로 설정
        String fileResourcePath = Paths.get(FOLDER_PATH, randomId).toString();  // 절대 경로 사용

        // 폴더 없으면 생성
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 파일 복사
        Files.copy(file.getInputStream(), Paths.get(fileResourcePath));

        // 파일 정보를 FileEntity로 저장
        FileEntity saveFile = FileEntity.builder()
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileType(file.getContentType())
                .build();
        saveFile.setMappingBoard(board);  // 게시글과 연관

        // 파일 정보 저장 후 FileEntity 반환
        FileEntity savedFileEntity = fileRepository.save(saveFile);
        return FileResDto.fromEntity(savedFileEntity); // FileResDto로 변환하여 반환
    }

    public ResponseEntity<Resource> getFile(Long fileId) {
        // 파일 엔티티를 가져옴
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "fileId", String.valueOf(fileId)));

        // 파일이 저장된 실제 경로 가져오기
        String filePath = fileEntity.getFilePath();  // 예시: "/uploads/images/myfile.jpg"

        // 해당 파일을 파일 시스템에서 읽기
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ResourceNotFoundException("File", "filePath", filePath);
        }

        // 파일을 Resource로 변환
        Resource fileResource = new FileSystemResource(file);

        // 파일이 이미지인 경우 MIME 타입을 자동으로 추론하여 설정
        String mimeType = "application/octet-stream"; // 기본 MIME 타입 (파일에 따라 다르게 설정할 수 있음)
        try {
            mimeType = Files.probeContentType(file.toPath()); // 파일의 MIME 타입을 자동으로 결정
        } catch (IOException e) {
            e.printStackTrace();
        }

        // HTTP 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

        // 파일을 응답으로 반환
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length()) // 파일 크기
                .body(fileResource);
    }

    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }
}