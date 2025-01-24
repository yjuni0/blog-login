package com.project.blog.entity;

import com.project.blog.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1024)
    private String content;

    private int viewCount;

    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    private FileEntity backgroundImage;

    @ManyToOne(fetch = FetchType.LAZY)
    public User user;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Comment> comments;

    @Builder
    public Board(Long id, String title, String content, int viewCount, User user, FileEntity backgroundImage, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.user = user;
        this.backgroundImage = backgroundImage;
        this.comments = comments;
    }
    //게시글 수정
    public void update (String title, String content) {
        this.title = title;
        this.content = content;
    }
    //조회수 증가
    public void upViewCount(String currentUser) {
        // 게시글 작성자와 현재 사용자가 다를 경우에만 조회수 증가
        if (!Objects.equals(this.user.getUsername(), currentUser)) {
            this.viewCount++;
        }
    }

    // 회원 ( 회원과의 관계 )
    public void setMappingMember(User user){
        this.user = user;
        user.getBoards().add(this);
    }
}
