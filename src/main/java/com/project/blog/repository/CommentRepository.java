package com.project.blog.repository;

import com.project.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글에 대한 댓글 목록을 페이지네이션과 함께 조회
    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH c.board b WHERE b.id = :boardId")
    Page<Comment> findAllWithUserAndBoard(Pageable pageable, Long boardId);

    // 특정 댓글을 조회하고 관련된 user와 board도 함께 가져옴
    @Query("SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH c.board b WHERE b.id = :commentId")
    Optional<Comment> findByWithUserAndBoard(Long commentId);

}
