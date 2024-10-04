package com.hf.healthfriend.domain.comment.repository.querydsl;

import com.hf.healthfriend.domain.comment.constant.SortType;
import com.hf.healthfriend.domain.comment.entity.Comment;

import java.util.List;

public interface CommentCustomRepository {
    List<Comment> findCommentsByPostIdWithSorting(Long postId, SortType sortType);

}
