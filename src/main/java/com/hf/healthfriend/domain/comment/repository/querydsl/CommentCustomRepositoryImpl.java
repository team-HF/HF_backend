package com.hf.healthfriend.domain.comment.repository.querydsl;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.entity.Comment;
import com.hf.healthfriend.domain.comment.entity.QComment;
import com.hf.healthfriend.domain.like.entity.QLike;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hf.healthfriend.domain.comment.constant.CommentSortType.LATEST;


@RequiredArgsConstructor
@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;
    private final QLike like = QLike.like;

    @Override
    public List<Comment> findCommentsByPostIdWithSorting(Long postId, CommentSortType sortType) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(like).on(like.comment.eq(comment))
                .where(comment.post.postId.eq(postId).and(comment.isDeleted.isFalse()))
                .groupBy(comment.commentId)
                .orderBy(getSortTypeByPostId(sortType))
                .fetch(); //리스트로 결과 반환하는 메소드
    }

    public OrderSpecifier<?>[] getSortTypeByPostId(CommentSortType sortType) {
        if (sortType == null) sortType = LATEST;
        switch (sortType) {
            case MOST_LIKES :
                return new OrderSpecifier<?>[]{
                        like.count().desc(),
                        comment.creationTime.desc()
                };
            default : return new OrderSpecifier<?>[]{
                    comment.creationTime.desc()
            };
        }
    }
}
