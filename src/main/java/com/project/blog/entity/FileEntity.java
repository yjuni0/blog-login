package com.project.blog.entity;

import com.project.blog.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class FileEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "file_id")
    private Long id;

    private String fileName;
    private String filePath;
    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @Builder
    public FileEntity(Long id, String fileName, String filePath, String fileType) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public void setMappingBoard(Board board) {
        this.board = board;
    }
}
