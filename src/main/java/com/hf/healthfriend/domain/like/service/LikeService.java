package com.hf.healthfriend.domain.like.service;

import com.hf.healthfriend.domain.like.dto.LikeDto;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.like.exception.DuplicateLikeException;
import com.hf.healthfriend.domain.like.exception.PostOrMemberNotExistsException;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;

    /**
     * 좋아요를 추가한다.
     *
     * @param memberId 좋아요를 남기는 회원의 ID. Not null
     * @param postId   좋아요를 남길 글의 ID. Not Null
     * @return 자동 생성된 Like entity의 ID
     * @throws DuplicateLikeException DuplicateLikeException 특정 회원이 특정 포스트에 대해 이미 좋아요를 남겼는데 중복해서
     *                                *                                좋아요를 남기려고 할 경우
     */
    public Long addLike(Long memberId, Long postId) throws DuplicateLikeException {
        Optional<Like> likeOp = this.likeRepository.findByMemberIdAndPostId(memberId, postId);
        if (likeOp.isEmpty()) {
            Like like = new Like(
                    new Member(memberId),
                    Post.builder()
                            .postId(postId)
                            .build()
            );
            try {
                Like savedLike = this.likeRepository.save(like);
                return savedLike.getLikeId();
            } catch (DataIntegrityViolationException e) {
                throw new PostOrMemberNotExistsException(e, memberId, postId);
            }
        }

        Like like = likeOp.get();

        if (!like.isCanceled()) {
            throw new DuplicateLikeException(postId, memberId);
        }

        like.uncancel();

        return like.getLikeId();
    }

    /**
     * likeId를 search key로 하여 단일 Like 정보를 가져온다.
     * 이 Service에 의존성을 가지는 객체에서 사용할 수 있으므로 남겨 놓음.
     *
     * @param likeId 찾고자 하는 Like의 ID
     * @return Like entity의 정보를 가진 Like entity
     * @throws NoSuchElementException likeId를 ID로 하는 Like entity가 없을 경우
     */
    public LikeDto getLike(Long likeId) throws NoSuchElementException {
        Like searchedLike = this.likeRepository.findById(likeId).orElseThrow(() ->
                new NoSuchElementException(String.format("존재하지 않는 Like입니다. ID=%d", likeId)));// TODO 하드코딩 고치기
        return LikeDto.of(searchedLike);
    }

    /**
     * 특정 회원이 특정 글에 좋아요를 남겼는지 확인
     *
     * @param memberId 좋아요를 남겼는지 체크할 회원의 ID
     * @param postId 회원이 좋아요를 남겼는지 체크할 Post의 ID
     * @return 해당 회원이 해당 글에 좋아요를 남겼으면 true, 그렇지 않으면 false
     */
    public boolean doesMemberLikePost(Long memberId, Long postId) {
        return this.likeRepository.existsByMemberIdAndPostId(memberId, postId);
    }

    public List<LikeDto> getLikeOfPost(Long postId) {
        return entityListToDtoList(this.likeRepository.findByPostId(postId));
    }

    public Long getLikeCountOfPost(Long postId) {
        return this.likeRepository.countByPostId(postId);
    }

    public List<LikeDto> getLikeOfMember(Long memberId) {
        return entityListToDtoList(this.likeRepository.findByMemberId(memberId));
    }

    private List<LikeDto> entityListToDtoList(List<Like> entities) {
        return entities.stream().map(LikeDto::of).toList();
    }

    public void cancelLike(Long likeIdToCancel) throws NoSuchElementException {
        Like likeEntity = this.likeRepository.findById(likeIdToCancel).orElseThrow(NoSuchElementException::new);// TODO: 메시지?
        likeEntity.cancel();
    }
}
