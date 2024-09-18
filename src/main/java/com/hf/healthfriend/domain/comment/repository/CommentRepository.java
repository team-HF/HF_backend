package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    public Comment save(Comment commentToSave) {
        return this.commentJpaRepository.save(commentToSave);
    }

    /**
     * Comment 논리 삭제. 논리 삭제이기 때문에 실제로 데이터베이스에서 레코드가 삭제되지 않고, is_deleted 칼럼이 true로 set된다.
     * @param commentId 논리 삭제를 수행할 대상 entity
     * @return commentId에 해당하는 Comment entity가 존재해서 삭제에 성공하면 true, 그렇지 않으면 false
     */
    public boolean deleteById(Long commentId) {
        Optional<Comment> commentOp = this.commentJpaRepository.findById(commentId);
        if (commentOp.isEmpty()) {
            return false;
        }
        Comment comment = commentOp.get();
        comment.setDeleted(true);
        return true;
    }

    public Optional<Comment> findById(Long commentId) {
        return this.commentJpaRepository.findById(commentId);
    }

    public List<Comment> findCommentsByPostId(Long postId) {
        return this.commentJpaRepository.findByPostId(postId);
    }

    // TODO: 추후 Pagination이 필요할 듯
    public List<Comment> findCommentsByWriterId(Long writerId) {
        return this.commentJpaRepository.findByWriterId(writerId);
    }

    public Comment updateComment(Long commentId, CommentUpdateDto updateDto) {
        Optional<Comment> commentOp = this.commentJpaRepository.findById(commentId);
        if (commentOp.isEmpty()) {
            throw new NoSuchElementException(); // TODO: 이 Exception 써도 되나?
        }
        Comment comment = commentOp.get();

        // TODO: Java Reflection을 활용해 유연한 update가 가능할 듯
        if (updateDto.getContent() != null) {
            comment.setContent(updateDto.getContent());
        }
        comment.setLastModified(LocalDateTime.now()); // TODO: Update DTO의 모든 null일 경우 어떻게 할까?
        return comment;
    }
}
