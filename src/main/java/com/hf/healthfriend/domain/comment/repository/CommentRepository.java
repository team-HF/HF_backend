package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
