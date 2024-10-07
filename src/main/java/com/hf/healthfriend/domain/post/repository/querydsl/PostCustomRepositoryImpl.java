package com.hf.healthfriend.domain.post.repository.querydsl;

import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.entity.QPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.creationTime);

    @Override
    public List<PostListObject> getList(FitnessLevel fitnessLevel, PostCategory postCategory, String keyword, Pageable pageable) {
        BooleanBuilder builder = filter(fitnessLevel, postCategory, keyword);
        return  queryFactory
                .selectFrom(post)
                .where(builder)
                .groupBy(post)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(post-> {
                    String content = post.getContent();
                    if (keyword!=null){
                        String sentence = getSentenceContainKeyword(keyword,post.getContent());
                        content = (sentence!=null)?sentence:content;
                    }
                    return PostListObject.builder()
                            .postId(post.getPostId())
                            .title(post.getTitle())
                            .category(post.getCategory().name())
                            .viewCount(post.getViewCount())
                            .creationTime(post.getCreationTime())
                            .content(content)
                            .fitnessLevel(post.getMember().getFitnessLevel().name())
                            .likeCount(post.getLikes().size())
                            .build();
                }).toList();
    }

    public BooleanBuilder filter(FitnessLevel fitnessLevel, PostCategory postCategory, String keyword) {
        // 조건을 동적으로 추가하기 위한 BooleanBuilder 생성
        BooleanBuilder builder = new BooleanBuilder();
        if (fitnessLevel != null) {
            builder.and(post.member.fitnessLevel.eq(fitnessLevel));
        }
        if (postCategory != null) {
            builder.and(post.category.eq(postCategory));
        }
        if (keyword != null) {
            builder.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }
        return builder;
    }

    public String getSentenceContainKeyword(String keyword, String content){
        String[] sentences = content.split("(?<=[.!?])");
        for (String sentence : sentences) {
            if (sentence.contains(keyword)) {
                return sentence.trim();
            }
        }
        return null;
    }
}