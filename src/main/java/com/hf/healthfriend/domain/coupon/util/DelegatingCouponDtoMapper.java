package com.hf.healthfriend.domain.coupon.util;

import com.hf.healthfriend.domain.coupon.dto.response.CouponResponseDto;
import com.hf.healthfriend.domain.coupon.entity.Coupon;
import com.hf.healthfriend.domain.coupon.exception.AmbiguousDelegatorException;
import com.hf.healthfriend.domain.coupon.exception.NoSupportedCouponDelegatorException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class DelegatingCouponDtoMapper implements CouponDtoMapper {
    private final Map<Class<?>, CouponDtoMappingDelegator> delegatorBySupportingType;

    public DelegatingCouponDtoMapper(List<CouponDtoMappingDelegator> delegators) {
        Map<Class<?>, CouponDtoMappingDelegator> delegatorMap = new HashMap<>();
        for (CouponDtoMappingDelegator delegator : delegators) {
            List<Class<?>> supportingTypes = delegator.getSupportingTypes();
            for (Class<?> supportingType : supportingTypes) {
                if (delegatorMap.containsKey(supportingType)) {
                    throw new AmbiguousDelegatorException("중복되는 Delegator for type - " + supportingType);
                }
                delegatorMap.put(supportingType, delegator);
            }
        }

        this.delegatorBySupportingType = Collections.unmodifiableMap(delegatorMap);
    }

    @Override
    public CouponResponseDto mapToCouponResponseDto(Coupon entity) {
        CouponDtoMappingDelegator delegator = this.delegatorBySupportingType.get(entity.getClass());
        if (delegator == null) {
            throw new NoSupportedCouponDelegatorException(entity.getClass());
        }
        return delegator.mapToCouponResponseDto(entity);
    }
}
