package com.hf.healthfriend.domain.coupon.service;

import com.hf.healthfriend.domain.coupon.dto.request.GrantCouponDto;
import com.hf.healthfriend.domain.coupon.exception.NoSupportedCouponDelegatorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiFunction;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DelegatingCouponService implements CouponService {
    private final List<CouponServiceDelegator> delegators;

    @Override
    public Long grantCoupon(GrantCouponDto dto) {
        return delegate((input, delegator) -> delegator.grantCoupon(input), dto);
    }

    private <P, R> R delegate(BiFunction<P, CouponServiceDelegator, R> logic, P input) {
        Class<?> clazz = input.getClass();
        for (CouponServiceDelegator delegator : this.delegators) {
            if (delegator.supports(clazz)) {
                return logic.apply(input, delegator);
            }
        }
        throw new NoSupportedCouponDelegatorException(clazz);
    }
}
