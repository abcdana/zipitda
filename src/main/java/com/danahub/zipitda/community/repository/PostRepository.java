package com.danahub.zipitda.community.repository;

import com.danahub.zipitda.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}