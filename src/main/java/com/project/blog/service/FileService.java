package com.project.blog.service;

import com.project.blog.common.exception.ResourceNotFoundException;
import com.project.blog.dto.response.file.FileResDto;
import com.project.blog.entity.Board;
import com.project.blog.entity.FileEntity;
import com.project.blog.repository.BoardRepository;
import com.project.blog.repository.FileEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    public FileResDto getFile(Long fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File", "fileId", String.valueOf(fileId)));
        return FileResDto.fromEntity(fileEntity);
    }

    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }
}