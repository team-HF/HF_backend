package com.hf.healthfriend.global.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YearAndMonthDeserializer extends JsonDeserializer<LocalDate> {
    private static final Pattern DATE_VALUE_PATTERN = Pattern.compile("^\\d{4}-\\d{1,2}$");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String dateValue = p.getValueAsString();
        Matcher matcher = DATE_VALUE_PATTERN.matcher(dateValue);
        if (!matcher.matches()) {
            throw new InvalidFormatException(p, "LocalDate 타입이 맞지 않습니다.", dateValue, LocalDate.class);
        }
        String[] parsed = dateValue.split("-");
        int year = Integer.parseInt(parsed[0]);
        int month = Integer.parseInt(parsed[1]);
        if (month > 12 || month < 1) {
            throw new InvalidFormatException(p, "month는 1 ~ 12 범위 안에 들어야 합니다.", dateValue, LocalDate.class);
        }
        return LocalDate.of(year, month, 1);
    }
}
