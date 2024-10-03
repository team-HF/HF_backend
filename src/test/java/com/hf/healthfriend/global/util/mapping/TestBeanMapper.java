package com.hf.healthfriend.global.util.mapping;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringJUnitConfig
@Import(TestBeanMapper.TestConfig.class)
class TestBeanMapper {

    @Autowired
    BeanMapper beanMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public BeanMapper beanMapper() throws ClassNotFoundException {
            BeanMapper beanMapper = new BeanMapper();
            beanMapper.init();
            return beanMapper;
        }
    }

    @DisplayName("generateBean - return Object")
    @Test
    void generateBean_object() {
        SampleSourceClass sampleSourceClass = new SampleSourceClass("f1", 13, "sample", null, null);

        Object result = this.beanMapper.generateBean(sampleSourceClass);
        SampleTargetClass sampleTargetClass = (SampleTargetClass)result;

        log.info("result={}", result);

        assertThat(sampleTargetClass.getField1()).isEqualTo("f1");
        assertThat(sampleTargetClass.getField2()).isEqualTo(13);
        assertThat(sampleTargetClass.getFieldOfOtherName()).isEqualTo("sample");
        assertThat(sampleTargetClass.getFieldSetNullTrue()).isNull();
        assertThat(sampleTargetClass.getFieldSetNullFalse()).isNull();
    }

    @DisplayName("generateBean - return Generic")
    @Test
    void generateBean_generic() {
        SampleSourceClass sampleSourceClass = new SampleSourceClass("f1", 13, "sample", null, null);

        SampleTargetClass result = this.beanMapper.generateBean(sampleSourceClass, SampleTargetClass.class);

        log.info("result={}", result);

        assertThat(result.getField1()).isEqualTo("f1");
        assertThat(result.getField2()).isEqualTo(13);
        assertThat(result.getFieldOfOtherName()).isEqualTo("sample");
        assertThat(result.getFieldSetNullTrue()).isNull();
        assertThat(result.getFieldSetNullFalse()).isNull();
    }

    @DisplayName("copyProperties")
    @Test
    void copyProperties() {
        SampleSourceClass source = new SampleSourceClass("f1", 13, "sample", null, null);
        SampleTargetClass target = new SampleTargetClass("target1", 2, "target2", "target3", "target4");

        this.beanMapper.copyProperties(source, target);

        assertThat(target.getField1()).isEqualTo("f1");
        assertThat(target.getField2()).isEqualTo(13);
        assertThat(target.getFieldOfOtherName()).isEqualTo("sample");
        assertThat(target.getFieldSetNullTrue()).isNull();
        assertThat(target.getFieldSetNullFalse()).isEqualTo("target4");
    }
}