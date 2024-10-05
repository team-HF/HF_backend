package com.hf.healthfriend.global.util.mapping;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
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
