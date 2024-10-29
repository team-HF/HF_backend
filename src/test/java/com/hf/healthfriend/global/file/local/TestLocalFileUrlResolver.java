package com.hf.healthfriend.global.file.local;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class TestLocalFileUrlResolver {

    final String serverOrigin = "http://localhost:8080";

    LocalFileUrlResolver localFileUrlResolver;

    @BeforeEach
    void beforeEach() {
        this.localFileUrlResolver = new LocalFileUrlResolver(this.serverOrigin);
    }

    static Stream<Arguments> generateFilePath_success() {
        return Stream.of(
                Arguments.of("filename.jpg", "filename.jpg", new String[0]),
                Arguments.of("filename", "filename", new String[0]),
                Arguments.of("image/filename.jpg", "filename.jpg", new String[] { "image" }),
                Arguments.of("files/image/filename.jpg", "filename.jpg", new String[] { "files", "image" })
        );
    }

    @DisplayName("generateFilePath - success")
    @MethodSource
    @ParameterizedTest
    void generateFilePath_success(String expected, String testFilename, String[] testPath) {
        String result = this.localFileUrlResolver.generateFilePath(testFilename, testPath);

        log.info("result={}", result);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("resolveFileUrl - success")
    @CsvSource(
            value = {
                    "http://localhost:8080/image/filename.jpg;image/filename.jpg",
                    "http://localhost:8080/image/filename.jpg;/image/filename.jpg",
                    "http://localhost:8080/filename.jpg;filename.jpg",
                    "http://localhost:8080/filename.jpg;/filename.jpg"
            },
            delimiter = ';'
    )
    @ParameterizedTest
    void resolveFileUrl_success(String expected, String filePath) {
        log.info("expected={}, filePath={}", expected, filePath);

        String result = this.localFileUrlResolver.resolveFileUrl(filePath);

        log.info("result={}", result);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("resolveFileUrl - null을 인자로 전달하면 null 반환")
    @Test
    void resolveFileUrl_null() {
        String result = this.localFileUrlResolver.resolveFileUrl(null);
        assertThat(result).isNull();
    }

    static Stream<Arguments> generateUploadUrl_success() {
        return Stream.of(
                Arguments.of(
                        "http://localhost:8080/hr/files/filename",
                        "filename",
                        new String[0]
                ),
                Arguments.of(
                        "http://localhost:8080/hr/files/image/filename.jpg",
                        "filename.jpg",
                        new String[] { "image" }
                ),
                Arguments.of(
                        "http://localhost:8080/hr/files/image/files/filename.jpg",
                        "filename.jpg",
                        new String[] { "image", "files" }
                )
        );
    }

    @DisplayName("generateUploadUrl - success")
    @MethodSource
    @ParameterizedTest
    void generateUploadUrl_success(String expected, String filename, String[] paths) {
        log.info("expected={}, filename={}, paths={}", expected, filename, Arrays.toString(paths));

        String result = this.localFileUrlResolver.generateUploadUrl(filename, paths);
        log.info("result={}", result);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("generateUploadUrl - null을 인자로 전달하면 null 반환")
    @Test
    void generateUploadUrl_nullAsParameter() {
        String result = this.localFileUrlResolver.generateUploadUrl(null);

        assertThat(result).isNull();
    }

    @DisplayName("generateUploadUrl - 빈 문자열을 인자로 전달하면 null 반환")
    @Test
    void generateUploadUrl_emptyStringAsParameter() {
        String result = this.localFileUrlResolver.generateUploadUrl("");

        assertThat(result).isNull();
    }
}