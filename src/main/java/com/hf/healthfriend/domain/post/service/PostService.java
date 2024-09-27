package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.comment.dto.CommentDto;
import com.hf.healthfriend.domain.comment.service.CommentService;
import com.hf.healthfriend.domain.like.service.LikeService;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.dto.response.PostListObject;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.exception.CustomException;
import com.hf.healthfriend.domain.post.exception.PostErrorCode;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final LikeService likeService;

    public Long save(PostWriteRequest postWriteRequest) {
        Long memberId = postWriteRequest.getWriterId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Post post = postWriteRequest.toEntity(member);
        return postRepository.save(post).getPostId();
    }

    public Long update(PostWriteRequest postWriteRequest, Long postId){
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        post.update(postWriteRequest.getTitle(), postWriteRequest.getContent(), PostCategory.valueOf(postWriteRequest.getCategory()));
        return post.getPostId();
    }


    public PostGetResponse get(Long postId,boolean canUpdateViewCount) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        if(canUpdateViewCount) {
            /**
             * TODO : 동시성 처리 필요
             */
            post.updateViewCount(post.getViewCount());
        }
        List<CommentDto> commentList = commentService.getCommentsOfPost(postId);
        Long likeCount = likeService.getLikeCountOfPost(postId);
        return PostGetResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .postCategory(post.getCategory().name())
                .viewCount(post.getViewCount())
                .comments(commentList)
                .createDate(post.getCreationTime())
                .likeCount(likeCount)
                .build();
    }

    public void delete(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        post.delete();
    }

    public List<PostListObject> getList(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber-1, 10,
                Sort.by("creationTime").descending());
        Page<PostListObject> postList = postRepository.findAll(pageable)
                .map(post -> PostListObject.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .category(post.getCategory().name())
                        .viewCount(post.getViewCount())
                        .creationTime(post.getCreationTime())
                        .commentCount(postRepository.countCommentsByPostId(post.getPostId()))
                        .content(post.getContent())
                        .fitnessLevel(post.getMember().getFitnessLevel().name())
                        //.likeCount(postRepository.countLikesByPostId(post.getPostId()))
                        .build());
        return postList.getContent();
    }

    public List<PostListObject> getsearchedList(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber-1, 10,
                Sort.by("creationTime").descending());
        Page<PostListObject> postList = postRepository.findByTitleContainingOrContentContaining(pageable,keyword)
                .map(post -> PostListObject.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .category(post.getCategory().name())
                        .viewCount(post.getViewCount())
                        .creationTime(post.getCreationTime())
                        .commentCount(postRepository.countCommentsByPostId(post.getPostId()))
                        .content(post.getContent())
                        .fitnessLevel(post.getMember().getFitnessLevel().name())
                        //.likeCount(postRepository.countLikesByPostId(post.getPostId()))
                        .build());
        return postList.getContent();
    }


}


