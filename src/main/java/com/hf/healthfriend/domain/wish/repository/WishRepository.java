package com.hf.healthfriend.domain.wish.repository;

import com.hf.healthfriend.domain.wish.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
    boolean existsByWishedIdAndWisherId(Long wishedId, Long wisherId);
    Optional<Wish> findByWishIdAndIsDeletedFalse(Long id);
    List<Wish> findAllByWisherIdAndIsDeletedFalse(Long memberId, Pageable pageable);
    List<Wish> findAllByWishedIdAndIsDeletedFalse(Long memberId, Pageable pageable);
}
