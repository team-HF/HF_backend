package com.hf.healthfriend.global.jackson.serailizer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;

public class YearAndMonthSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getYear() + "-" + getMonthFormat(value.getMonthValue()));
    }

    private String getMonthFormat(int monthValue) {
        return monthValue < 10 ? "0" + monthValue : String.valueOf(monthValue);
    }
}
