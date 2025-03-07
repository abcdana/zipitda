package com.danahub.zipitda.community.repository;

import com.danahub.zipitda.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 좋아요 개수 조회
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetId = :postId AND l.targetType = 'POST'")
    int countLikesByPostId(Long postId);

    // 북마크 개수 조회
    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.targetId = :postId AND b.targetType = 'POST'")
    int countBookmarksByPostId(Long postId);

    // 페이징 및 정렬 적용된 게시글 목록 조회
    Page<Post> findAll(Pageable pageable);
}