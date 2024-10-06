package com.hf.healthfriend.global.util.mapping;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@BeanMapping(SampleTargetClass.class)
@ToString
public class SampleSourceClass {
    private String field1;
    private int field2;

    @MappingAttribute(target = "fieldOfOtherName")
    private String field3;

    @MappingAttribute(target = "fieldSetNullTrue", setNull = true)
    private String field4;

    @MappingAttribute(target = "fieldSetNullFalse", setNull = false)
    private String field5;
}
