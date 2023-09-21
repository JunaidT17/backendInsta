package com.example.projetinsta.repository;

import com.example.projetinsta.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    Page<Post> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM TICKET WHERE seller_id = ?1", nativeQuery = true)
    List<Post> findPostOnSale(Long userId);

    @Query(value = "SELECT * FROM TICKET WHERE buyer_id = ?1", nativeQuery = true)
    List<Post> findPostPurchased(Long userId);
}


