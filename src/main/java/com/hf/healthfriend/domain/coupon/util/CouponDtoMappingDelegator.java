package com.hf.healthfriend.domain.coupon.util;

import java.util.List;

public interface CouponDtoMappingDelegator extends CouponDtoMapper {

    List<Class<?>> getSupportingTypes();
}
