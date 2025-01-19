package com.project.blog.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass //@MappedSuperclass 해당 클래스가 엔티티 클래스의 공통 속성을 정의하는 부모클래스 직접적으로 db 테이블 매핑은 x but 하위 클래스 매핑의 영향
@EntityListeners(AuditingEntityListener.class) //엔티티 생성 및 수정시간 자동 처리 필드의 @createdDate 와 @LastModifiedDate 와 함께쓰임
public class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private String createdDate;

    @LastModifiedDate
    @Column(name="modified_date")
    private String modifiedDate;

    @PrePersist //엔티티가 영속화 되기 전에 실행
    public void onPrePersist(){
        this.createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.modifiedDate=this.createdDate;
    }
    @PreUpdate //엔티티가 업데이트 되기 직전에 실행
    public void onPreUpdate(){
        this.modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
