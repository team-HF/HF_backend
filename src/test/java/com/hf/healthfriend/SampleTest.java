package com.hf.healthfriend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@ActiveProfiles({
        "local-dev",
        "secret",
        "priv",
        "constants"
})
@SpringBootTest
public class SampleTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void test() throws Exception {
        SampleDto sampleDto = this.objectMapper.readValue("""
                {
                    "localDate": "2024-03"
                }
                """, SampleDto.class);

        System.out.println("sampleDto=" + sampleDto);
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class SampleDto {
        @JsonFormat(pattern = "yyyy-mm")
        LocalDate localDate;
    }
}
