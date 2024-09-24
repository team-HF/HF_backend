package com.hf.healthfriend.domain.like.service;

import com.hf.healthfriend.domain.like.dto.LikeDto;
import com.hf.healthfriend.domain.like.entity.Like;
import com.hf.healthfriend.domain.like.exception.DuplicateLikeException;
import com.hf.healthfriend.domain.like.repository.LikeRepository;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
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
     * @param likeDto likeDto.memberId != null && likeDto.postId != null
     *                likeDto.likeId는 nullable
     * @return 자동 생성된 Like entity의 ID
     * @throws DuplicateLikeException 특정 회원이 특정 포스트에 대해 이미 좋아요를 남겼는데 중복해서
     *                                좋아요를 남기려고 할 경우
     */
    public Long addLike(LikeDto likeDto) throws DuplicateLikeException {
        Long memberId = likeDto.getMemberId();
        Long postId = likeDto.getPostId();
        Optional<Like> likeOp = this.likeRepository.findByMemberIdAndPostId(memberId, postId);
        if (likeOp.isEmpty()) {
            Post post = Post.builder()
                    .postId(likeDto.getPostId())
                    .build();
            Member member = new Member(likeDto.getMemberId());
            Like like = new Like(member, post);
            Like savedLike = this.likeRepository.save(like);
            return savedLike.getLikeId();
        }

        Like like = likeOp.get();

        if (!like.isCanceled()) {
            throw new DuplicateLikeException(likeDto.getPostId(), likeDto.getMemberId());
        }

        like.uncancel();

        return like.getLikeId();
    }

    /**
     * likeId를 search key로 하여 단일 Like 정보를 가져온다.
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

    public List<LikeDto> getLikeOfPost(Long postId) {
        return entityListToDtoList(this.likeRepository.findByPostId(postId));
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
