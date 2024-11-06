package com.hf.healthfriend.domain.matching.repository;

import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.entity.QMatching;
import com.hf.healthfriend.testutil.TestConfig;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestMatchingRepository {

//    @Autowired
//    MatchingRepository matchingRepository;

    QMatching matching = QMatching.matching;

    @Test
    void test() {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(this.matching.requester.id.eq(1000L));
        builder.or(this.matching.targetMember.id.eq(1000L));
        builder.and(this.matching.status.eq(MatchingStatus.ACCEPTED));
        builder.and(null);
        System.out.println(builder);
    }
}