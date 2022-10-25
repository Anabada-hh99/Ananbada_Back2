package com.example.advanced.repository;


import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  Page<Post> findByCategory(PostCategory category, Pageable pageable);
  @Modifying
  @Query("update Post p set p.count = p.count + 1 where p.postId = :postId")    int updateView(Long postId);



}
