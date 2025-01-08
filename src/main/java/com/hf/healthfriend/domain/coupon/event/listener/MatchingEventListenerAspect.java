package com.hf.healthfriend.domain.coupon.event.listener;

import com.hf.healthfriend.domain.coupon.event.message.MatchingEventMessage;
import com.hf.healthfriend.domain.matching.constant.MatchingStatus;
import com.hf.healthfriend.domain.matching.entity.Matching;
import com.hf.healthfriend.domain.matching.repository.MatchingRepository;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.domain.Tier;
import com.hf.healthfriend.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Aspect
@Component
@Transactional
@RequiredArgsConstructor
public class MatchingEventListenerAspect {
    private final MatchingRepository matchingRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 매칭이 종료될 시점에 매칭 카운트가 올라감. 매칭 카운트가 올라갈 때 레벨이 올라가기 때문에,
     * MatchingService.updateMatchingStatus 메소드의 두 번째 파라미터인 MatchingStatus가
     * FINISHED일 경우에만 이 Advice가 호출됨.
     */
    @Around("execution(public void updateMatchingStatus(..))")
    public Object detectMatchingStatusUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long matchingId = (Long)args[0];
        MatchingStatus matchingStatus = (MatchingStatus)args[1];

        if (matchingStatus != MatchingStatus.FINISHED) {
            return joinPoint.proceed(args);
        }
        Matching matching = this.matchingRepository.findById(matchingId)
                .orElseThrow(NoSuchElementException::new);
        // TODO: LAZY 로딩으로 인한 성능 부하 예상됨
        Member requester = matching.getRequester();
        Tier tierBeforeMatchingFinished = requester.getTier();

        if (tierBeforeMatchingFinished.getFitnessLevel() == FitnessLevel.BEGINNER) {
            return joinPoint.proceed(args);
        }

        Object targetReturnValue = joinPoint.proceed(args);

        // Persistence Context에서 가져오는 것이기 때문에 그냥 몇 줄 위에 있는 requester를 써도 되겠으나,
        // JPA와의 decoupling을 위해 Repository의 findById 메소드 호출
        Matching afterMatching = this.matchingRepository.findById(matchingId)
                .orElseThrow(NoSuchElementException::new);

        Tier tierAfterMatchingFinished = afterMatching.getRequester().getTier();

        this.eventPublisher.publishEvent(new MatchingEventMessage(requester.getId(),
                tierBeforeMatchingFinished, tierAfterMatchingFinished));

        return targetReturnValue;
    }
}
