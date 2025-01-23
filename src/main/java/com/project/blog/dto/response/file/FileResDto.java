package com.project.blog.dto.response.file;

import com.project.blog.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FileResDto {
    private Long fileId;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime createdTime;

    @Builder
    public FileResDto(Long fileId, String fileName, String filePath, String fileType, LocalDateTime createdTime) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.createdTime = createdTime;
    }
    public static FileResDto fromEntity(FileEntity fileEntity) {
        return FileResDto.builder()
                .fileId(fileEntity.getId())
                .fileName(fileEntity.getFileName())
                .filePath(fileEntity.getFilePath())
                .fileType(fileEntity.getFileType())
                .createdTime(fileEntity.getCreatedDate())
                .build();
    }

}
