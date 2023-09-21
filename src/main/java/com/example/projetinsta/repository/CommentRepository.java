package com.example.projetinsta.repository;

import com.example.projetinsta.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}


