package com.hf.healthfriend.domain.comment.repository;

import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.exception.CommentNotFoundException;
import com.hf.healthfriend.domain.comment.repository.dto.CommentUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
     * @throws CommentNotFoundException commentId에 해당하는 Comment entity가 존재하지 않을 경우
     */
    public void deleteById(Long commentId) throws CommentNotFoundException {
        Optional<Comment> commentOp = this.commentJpaRepository.findById(commentId);
        if (commentOp.isEmpty()) {
            throw new CommentNotFoundException("Comment entity of commentId not exists", commentId);
        }
        Comment comment = commentOp.get();
        comment.setDeleted(true);
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

    /**
     * Repository에 있는 Comment entity를 수정한다. 파라미터로 넘겨지는 updateDto 인스턴스의 필드 값 중 null 값은 무시된다.
     *
     * @param commentId 수정할 Comment의 ID
     * @param updateDto Comment의 값은 이 DTO 인스턴스에 저장된 값으로 변경된다. 필드에 null이 존재해도 된다
     * @return 수정된 Comment entity
     * @throws CommentNotFoundException commentId에 해당하는 Comment entity가 존재하지 않을 경우
     */
    public Comment updateComment(Long commentId, CommentUpdateDto updateDto) throws CommentNotFoundException {
        Optional<Comment> commentOp = this.commentJpaRepository.findById(commentId);
        if (commentOp.isEmpty()) {
            throw new CommentNotFoundException("Comment entity of commentId not exists", commentId);
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
