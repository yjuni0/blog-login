package com.project.blog.repository;

import com.project.blog.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.id= :boardId")
    Optional<Board> findById(Long boardId);

    @Query(value = "SELECT b FROM Board b JOIN fetch b.user")
    Page<Board> findAll(Pageable pageable);

    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.title LIKE %:title%")
    Page<Board> findByTitleContaining(String title, Pageable pageable);

    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.content LIKE %:content%")
    Page<Board> findByContentContaining(String content, Pageable pageable);

    @Query(value = "SELECT b FROM Board b JOIN FETCH b.user WHERE b.user.email LIKE %:writer%")
    Page<Board> findByWriter(String writer, Pageable pageable);


}
