package com.hf.healthfriend.domain.like.service;

import com.hf.healthfriend.domain.like.dto.LikeDto;
import com.hf.healthfriend.domain.like.exception.DuplicateLikeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LikeService {

    /**
     * 좋아요를 추가한다.
     *
     * @param likeDto likeDto.memberId != null && likeDto.postId != null
     *                likeDto.likeId는 nullable
     * @return 자동 생성된 Like entity의 ID
     * @throws DuplicateLikeException 특정 회원이 특정 포스트에 대해 이미 좋아요를 남겼는데 중복해서
     *                                좋아요를 남기려고 할 경우
     */
    public Long addLike(LikeDto likeDto) throws DuplicateLikeException {
        throw new UnsupportedOperationException();
    }

    /**
     * likeId를 search key로 하여 단일 Like 정보를 가져온다.
     *
     * @param likeId 찾고자 하는 Like의 ID
     * @return Like entity의 정보를 가진 Like entity
     * @throws NoSuchElementException likeId를 ID로 하는 Like entity가 없을 경우
     */
    public LikeDto getLike(Long likeId) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    public List<LikeDto> getLikeOfPost(Long postId) {
        throw new UnsupportedOperationException();
    }

    public List<LikeDto> getLikeOfMember(Long memberId) {
        throw new UnsupportedOperationException();
    }

    public void cancelLike(Long likeIdToCancel) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }
}
