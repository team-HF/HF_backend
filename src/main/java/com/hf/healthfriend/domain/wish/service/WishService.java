package com.hf.healthfriend.domain.wish.service;

import static com.hf.healthfriend.global.exception.ErrorCode.BAD_WISH_REQUEST;
import static com.hf.healthfriend.global.exception.ErrorCode.MEMBER_OF_THE_MEMBER_ID_NOT_FOUND;
import static com.hf.healthfriend.global.exception.ErrorCode.NON_EXIST_WISH;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.wish.dto.response.WishResponse;
import com.hf.healthfriend.domain.wish.entity.Wish;
import com.hf.healthfriend.domain.wish.repository.WishRepository;
import com.hf.healthfriend.global.exception.CustomException;
import jakarta.transaction.Transactional;
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
    private final MemberJpaRepository memberJpaRepository;

    public Long save(long wisherId, long wishedId){

        if (wishRepository.existsByWishedIdAndWisherId(wishedId,wisherId))
            throw new CustomException(BAD_WISH_REQUEST,HttpStatus.BAD_REQUEST);

        if(memberJpaRepository.existsById(wisherId) &&
        memberJpaRepository.existsById(wishedId)){
            Wish wish = new Wish(
                    new Member(wisherId),
                    new Member(wishedId));
            wishRepository.save(wish);
            return wish.getWishId();
        }
        throw new CustomException(MEMBER_OF_THE_MEMBER_ID_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }

    public void delete(long wishId){
        Wish wish = wishRepository.findByWishIdAndIsDeletedFalse(wishId)
                .orElseThrow(() -> new CustomException(NON_EXIST_WISH, HttpStatus.BAD_REQUEST));
        wish.delete();
    }

    public List<WishResponse> getWishedList(int page, int size, long memberId){
        Pageable pageable = PageRequest.of(page - 1, size);
        List<Wish> wishedList = wishRepository.findAllByWisherIdAndIsDeletedFalse(memberId, pageable);
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

    public List<WishResponse> getWisherList(int page, int size, long memberId){
        Pageable pageable = PageRequest.of(page - 1, size);
        List<Wish> wisherList = wishRepository.findAllByWishedIdAndIsDeletedFalse(memberId, pageable);
        List<WishResponse> wishResponseList = new ArrayList<>();
        for(Wish wish : wisherList){
            WishResponse wishResponse = WishResponse.builder()
                    .wishedId(wish.getWished().getId())
                    .wisherId(wish.getWisher().getId())
                    .build();
            wishResponseList.add(wishResponse);
        }
        return wishResponseList;
    }
}
