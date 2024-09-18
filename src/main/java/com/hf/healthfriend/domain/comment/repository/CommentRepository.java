package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public List<Comment> findCommentsByPostId(Long postId) {
        return this.commentJpaRepository.findByPostId(postId);
    }
}
