package com.hf.healthfriend.domain.wish.service;

import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.repository.MemberJpaRepository;
import com.hf.healthfriend.domain.wish.dto.response.WishResponse;
import com.hf.healthfriend.domain.wish.entity.Wish;
import com.hf.healthfriend.domain.wish.repository.WishRepository;
import com.hf.healthfriend.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WishServiceTest {

    @Mock
    private WishRepository wishRepository;

    @Mock
    private MemberJpaRepository memberJpaRepository;

    @InjectMocks
    private WishService wishService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("찜 성공")
    void testSaveWish_Success() {
        long wisherId = 1L;
        long wishedId = 2L;

        when(wishRepository.existsByWishedIdAndWisherId(wishedId, wisherId)).thenReturn(false);
        when(memberJpaRepository.existsById(wisherId)).thenReturn(true);
        when(memberJpaRepository.existsById(wishedId)).thenReturn(true);

        Wish savedWish = new Wish(new Member(wisherId), new Member(wishedId));
        when(wishRepository.save(any(Wish.class))).thenReturn(savedWish);

        Long wishId = wishService.save(wisherId, wishedId);

        verify(wishRepository, times(1)).save(any(Wish.class));
    }

    @Test
    @DisplayName("이미 존재하는 찜이면 예외")
    void testSaveWish_AlreadyExists() {
        long wisherId = 1L;
        long wishedId = 2L;

        when(wishRepository.existsByWishedIdAndWisherId(wishedId, wisherId)).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () -> {
            wishService.save(wisherId, wishedId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(wishRepository, never()).save(any(Wish.class));
    }

    @Test
    @DisplayName("멤버가 존재하지 않을 때")
    void testSaveWish_MemberNotFound() {
        long wisherId = 1L;
        long wishedId = 2L;

        when(wishRepository.existsByWishedIdAndWisherId(wishedId, wisherId)).thenReturn(false);
        when(memberJpaRepository.existsById(wisherId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> {
            wishService.save(wisherId, wishedId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(wishRepository, never()).save(any(Wish.class));
    }

    @Test
    @DisplayName("Soft Delete 테스트")
    void testDeleteWish_Success() {
        long wishId = 1L;
        Wish wish = new Wish();

        when(wishRepository.findByWishIdAndIsDeletedFalse(wishId)).thenReturn(Optional.of(wish));

        wishService.delete(wishId);

        verify(wishRepository, times(1)).findByWishIdAndIsDeletedFalse(wishId);
        assertTrue(wish.isDeleted());
    }

    @Test
    @DisplayName("존재하지 않는 찜을 삭제하려 할 때")
    void testDeleteWish_NotFound() {
        long wishId = 1L;

        when(wishRepository.findByWishIdAndIsDeletedFalse(wishId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            wishService.delete(wishId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(wishRepository, times(1)).findByWishIdAndIsDeletedFalse(wishId);
    }

    @Test
    @DisplayName("찜 목록 조회")
    void testGetWishedList() {
        long memberId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Member wisher = new Member(1L);
        Member wished = new Member(2L);
        Wish wish = new Wish(wisher, wished);
        List<Wish> wishes = Arrays.asList(wish);

        when(wishRepository.findAllByWisherIdAndIsDeletedFalse(memberId, pageable)).thenReturn(wishes);

        List<WishResponse> wishedList = wishService.getWishedList(page, size, memberId);

        assertEquals(1, wishedList.size());
        verify(wishRepository, times(1)).findAllByWisherIdAndIsDeletedFalse(memberId, pageable);
    }

    @Test
    @DisplayName("특정인 찜 목록 조회")
    void testGetWisherList() {
        long memberId = 1L;
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Member wisher = new Member(1L);
        Member wished = new Member(2L);
        Wish wish = new Wish(wisher,wished);
        List<Wish> wishes = Arrays.asList(wish);

        when(wishRepository.findAllByWishedIdAndIsDeletedFalse(memberId, pageable)).thenReturn(wishes);

        List<WishResponse> wisherList = wishService.getWisherList(page, size, memberId);

        assertEquals(1, wisherList.size());
        verify(wishRepository, times(1)).findAllByWishedIdAndIsDeletedFalse(memberId, pageable);
    }
}