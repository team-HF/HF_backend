package com.hf.healthfriend.global.jackson.serailizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hf.healthfriend.global.jackson.deserializer.YearAndMonthDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
class TestYearAndMonthSerialization {

    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    static class TestDto {

        @JsonSerialize(using = YearAndMonthSerializer.class)
        @JsonDeserialize(using = YearAndMonthDeserializer.class)
        LocalDate yearAndMonth;
    }

    @DisplayName("Serialization - success")
    @Test
    void serialization_success() throws Exception {
        // Given
        LocalDate date = LocalDate.of(2024, 9, 1);

        // When
        String result = this.objectMapper.writeValueAsString(new TestDto(date));
        log.info("result={}", result);

        // Then
        JSONObject jsonObject = new JSONObject(result);
        assertThat(jsonObject.getString("yearAndMonth")).isEqualTo("2024-09");
    }

    @DisplayName("Deserialization - success")
    @ValueSource(strings = {
            """
                    {
                        "yearAndMonth": "2024-03"
                    }
                    """,
            """
                    {
                        "yearAndMonth": "2024-3"
                    }
                    """
    })
    @ParameterizedTest
    void deserialization_success(String json) throws Exception {
        // When
        TestDto result = this.objectMapper.readValue(json, TestDto.class);
        log.info("result={}", result);

        // Then
        assertThat(result.getYearAndMonth()).isEqualTo(LocalDate.of(2024, 3, 1));
    }

    @DisplayName("Deserialization - 날짜 형식이 맞지 않아서 실패 - InvalidFormatException")
    @ValueSource(strings = {
            """
                    {
                        "yearAndMonth": "2024-03-01"
                    }
                    """,
            """
                    {
                        "yearAndMonth": "2024-ad"
                    }
                    """,
            """
                    {
                        "yearAndMonth": "2021-4-152"
                    }
                    """,
            """
                    {
                        "yearAndMonth": "24-08"
                    }
                    """
    })
    @ParameterizedTest
    void deserialization_invalidDateFormat(String json) {
         assertThatExceptionOfType(InvalidFormatException.class)
                 .isThrownBy(() -> this.objectMapper.readValue(json, TestDto.class));
    }

    @DisplayName("Deserialization - 월 범위가 초과해서 실패 - InvalidFormatException")
    @ValueSource(strings = {
            """
                    {
                        "yearAndMonth": "2024-13"
                    }
                    """,
            """
                    {
                        "yearAndMonth": "2024-0"
                    }
                    """
    })
    @ParameterizedTest
    void deserialization_invalidMonthRange(String json) {
        assertThatExceptionOfType(InvalidFormatException.class)
                .isThrownBy(() -> this.objectMapper.readValue(json, TestDto.class));
    }
}