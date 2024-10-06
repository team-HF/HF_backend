package com.hf.healthfriend.global.util.mapping;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class SampleTargetClass {
    private String field1;
    private int field2;
    private String fieldOfOtherName;
    private String fieldSetNullTrue;
    private String fieldSetNullFalse;
}
