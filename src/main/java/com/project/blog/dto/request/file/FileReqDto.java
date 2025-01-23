package com.project.blog.dto.request.file;

import com.project.blog.dto.response.file.FileResDto;
import com.project.blog.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FileReqDto {

    private Long fileId;
    private String fileName;
    private String filePath;
    private String fileType;

    @Builder
    public FileReqDto(Long fileId, String fileName, String filePath, String fileType, LocalDateTime createdTime) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public static FileEntity ofEntity(FileReqDto fileReqDto) {
        return FileEntity.builder()
                .id(fileReqDto.getFileId())
                .fileName(fileReqDto.getFileName())
                .filePath(fileReqDto.getFilePath())
                .fileType(fileReqDto.getFileType())
                .build();
    }
}
