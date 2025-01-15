package com.hf.healthfriend.domain.wish.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberRepository;
import com.hf.healthfriend.domain.wish.dto.response.WishResponse;
import com.hf.healthfriend.domain.wish.entity.Wish;
import com.hf.healthfriend.domain.wish.exception.WishErrorCode;
import com.hf.healthfriend.domain.wish.exception.WishException;
import com.hf.healthfriend.domain.wish.repository.WishRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;

    public Long save(Long wisherId, Long wishedId){

        if (wishRepository.existsByWishedIdAndWisherIdAndIsDeletedFalse(wishedId,wisherId))
            throw new WishException(WishErrorCode.DUPLICATE_WISH,HttpStatus.BAD_REQUEST,
                    "wishedId: "+wishedId+", wisherId: "+wisherId);

        boolean wisherExist = memberRepository.existsById(wisherId);
        boolean wishedExist = memberRepository.existsById(wishedId);
        if( wisherExist && wishedExist){
            Wish wish = new Wish(
                    new Member(wisherId),
                    new Member(wishedId));
            wishRepository.save(wish);
            memberRepository.findById(wishedId).get().incrementWishedCount();
            return wish.getWishId();
        }else if(wisherExist){
            throw new WishException(WishErrorCode.MEMBER_NOT_FOUND, HttpStatus.BAD_REQUEST,
                    "wishedId not found: "+wishedId);
        }else if(wishedExist){
            throw new WishException(WishErrorCode.MEMBER_NOT_FOUND, HttpStatus.BAD_REQUEST,
                    "wisherId not found: "+wisherId);
        }else{
            throw new WishException(WishErrorCode.MEMBER_NOT_FOUND, HttpStatus.BAD_REQUEST,
                    "wisherId not found: "+wisherId+" wishedId not found: "+wishedId);
        }
    }

    public void delete(Long wishId){
        Wish wish = wishRepository.findByWishIdAndIsDeletedFalse(wishId)
                .orElseThrow(() -> new WishException(WishErrorCode.WISH_NOT_FOUND, HttpStatus.BAD_REQUEST,
                        "wishId: "+wishId));
        wish.delete();
        Member member = wish.getWished();
        member.decrementWishedCount();
    }

    public List<WishResponse> getWishedList(int page, int size, Long memberId){
        Pageable pageable = PageRequest.of(page - 1, size);
        List<Wish> wishedList = wishRepository.findAllByWisherIdAndIsDeletedFalse(memberId, pageable);
        return getWishResponses(wishedList);
    }

    public List<WishResponse> getWisherList(int page, int size, Long memberId){
        Pageable pageable = PageRequest.of(page - 1, size);
        List<Wish> wisherList = wishRepository.findAllByWishedIdAndIsDeletedFalse(memberId, pageable);
        return getWishResponses(wisherList);
    }

    @NotNull
    private List<WishResponse> getWishResponses(List<Wish> wishedList) {
        List<WishResponse> wishResponseList = new ArrayList<>();
        for(Wish wish : wishedList){
            WishResponse wishResponse = WishResponse.builder()
                    .wishedId(wish.getWished().getId())
                    .wisherId(wish.getWisher().getId())
                    .build();
            wishResponseList.add(wishResponse);
        }
        return wishResponseList;
    }
}
