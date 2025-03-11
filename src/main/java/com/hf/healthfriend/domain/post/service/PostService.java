package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.comment.constant.CommentSortType;
import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.exception.PostErrorCode;
import com.hf.healthfriend.domain.post.exception.PostException;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import com.hf.healthfriend.global.file.FileUrlResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final RedissonClient redissonClient;

    private final FileUrlResolver fileUrlResolver;

    public Long save(PostWriteRequest postWriteRequest){
        Long memberId = postWriteRequest.getWriterId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Post post = postWriteRequest.toEntity(member);
        return postRepository.save(post).getPostId();
    }

    public Long update(PostWriteRequest postWriteRequest, Long postId){
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND,postId + "번 post가 존재하지 않습니다."));
        post.update(postWriteRequest.getTitle(), postWriteRequest.getContent(), PostCategory.valueOf(postWriteRequest.getCategory()));
        return post.getPostId();
    }

    public PostGetResponse get(Long postId, boolean canUpdateViewCount, CommentSortType sortType) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND, postId + "번 post가 존재하지 않습니다."));

        long viewCount = increaseViewCount(postId,post,canUpdateViewCount);
        List<CommentDto> commentList = commentService.getCommentsOfPost(postId, sortType);

        String imagePath = fileUrlResolver.resolveFileUrl(post.getImagePath());
        String writerProfileImageUrl = fileUrlResolver.resolveFileUrl(post.getMember().getProfileImageUrl());

        return PostGetResponse.of(post, commentList, viewCount, imagePath, writerProfileImageUrl);
    }

    public void delete(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND, HttpStatus.NOT_FOUND,postId + "번 post가 존재하지 않습니다."));
        post.delete();
        // Redis 에서도 조회수 제거
        String redisKey = "post:viewCount:" + postId;
        redissonClient.getAtomicLong(redisKey).delete();
        likeRepository.deleteLikeByPostId(postId);
    }

    public List<PostListObject> getList(int pageNumber, int size,FitnessLevel fitnessLevel, PostCategory postCategory, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        return postRepository.getList(fitnessLevel, postCategory, keyword, pageable);
    }

    public List<PostListObject> getPopularList(int pageNumber, int size,FitnessLevel fitnessLevel, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet("popular_posts");
        List<Long> postIdList = new ArrayList<>( sortedSet.readAll().stream().toList());
        return postRepository.getPopularList(postIdList,fitnessLevel,keyword,pageable);
    }

    private Long increaseViewCount(Long postId, Post post, boolean canUpdateViewCount) {
        String redisKey = "post:viewCount:" + postId;
        long viewCount = redissonClient.getAtomicLong(redisKey).get();

        // AtomicLong 은 기본 값이 0이므로 post 저장 시 레디스에 저장할 필요 없음
        if (viewCount == 0) { // 값이 존재하지 않는 경우
            viewCount = post.getViewCount(); // DB의 조회수 값 사용
            redissonClient.getAtomicLong(redisKey).set(viewCount); // Redis 에 초기화
            log.info("Redis 에 조회수 초기화: postId={}, newViewCount={}", postId, viewCount);
        }

        // 조회수 증가
        if (canUpdateViewCount) {
            viewCount = redissonClient.getAtomicLong(redisKey).incrementAndGet(); // INCR 과 동일한 동작
            log.info("조회수 증가: postId={}, newViewCount={}", postId, viewCount);
        }

        return viewCount;
    }
}
