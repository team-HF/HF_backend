package com.hf.healthfriend.domain.post.service;

import com.hf.healthfriend.testutil.RedisTestConfig;
import com.hf.healthfriend.testutil.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, RedisTestConfig.class})
class PopularPostListTest {

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        // Redis 데이터 초기화
        redissonClient.getKeys().flushall();

        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet("popular_posts");
        sortedSet.add(-10.0, 1L);
        sortedSet.add(-5.0, 2L);
        sortedSet.add(-15.0, 3L);
        sortedSet.add(-10.0, 4L);
        sortedSet.add(-5.0, 5L);
        sortedSet.add(-15.0, 6L);
        System.out.println("Redis Sorted Set: " + sortedSet.readAll());
    }

    @Test
    @DisplayName("인기글 목록 조회 - 기본 조회")
    void testGetPopularList_basic() {

        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet("popular_posts");
        List<Long> postIdList = new ArrayList<>( sortedSet.readAll().stream().toList());

        assertThat(postIdList).isNotNull();
        assertThat(postIdList.size()).isEqualTo(6);
        assertThat(postIdList.get(0)).isEqualTo(3L);
    }

    @Test
    @DisplayName("인기글 목록 totalPageSize 검증")
    void testGetPopularList_totalPageSize() {
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet("popular_posts");
        List<Long> postIdList = new ArrayList<>( sortedSet.readAll().stream().toList());
        long totalPageSize = (long) Math.ceil((double) postIdList.size() / 5);
        assertThat(totalPageSize).isEqualTo(2);

    }


}