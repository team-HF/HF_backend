package com.hf.healthfriend.global.file.image;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ImageExtension {
    // 프론트에서 아예
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
    WEBP(".webp");

    private final String value;

    public String value() {
        return this.value;
    }
}
