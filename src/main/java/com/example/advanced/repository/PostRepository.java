package com.example.advanced.repository;


import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findByCategory(PostCategory category);


}
