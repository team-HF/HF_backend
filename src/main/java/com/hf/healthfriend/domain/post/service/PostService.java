package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.post.constant.PostCategory;
import com.hf.healthfriend.domain.post.dto.request.PostWriteRequest;
import com.hf.healthfriend.domain.post.dto.response.PostGetResponse;
import com.hf.healthfriend.domain.post.entity.Post;
import com.hf.healthfriend.domain.post.exception.CustomException;
import com.hf.healthfriend.domain.post.exception.PostErrorCode;
import com.hf.healthfriend.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Long save(PostWriteRequest postWriteRequest, BearerTokenAuthentication authentication) {
        String memberId = authentication.getName();
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


    public PostGetResponse get(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        /** 비회원 조회수 -> 쿠키, 회원 조회수 -> Redis 리팩토링 필요**/
        post.updateViewCount(post.getViewCount());
        return PostGetResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .postCategory(post.getCategory().name())
                .view_count(post.getViewCount())
                .createDate(post.getCreationTime())
                .build();
    }

    public void delete(Long postId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.NON_EXIST_POST, HttpStatus.NOT_FOUND));
        post.delete();
    }

}
