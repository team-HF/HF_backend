package com.hf.healthfriend.global.util.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 따로 Profile이 설정되지 않으면 Spring Bean으로 등록된다.
 *
 * @author PGD
 * @see com.hf.healthfriend.global.config.BeanConfig
 */
@Slf4j
@RequiredArgsConstructor
public class LocalFileUrlResolver implements FileUrlResolver {
    private final String serverOrigin;

    @Override
    public String resolveFilePath(String filename, String... paths) {
        return File.separator + String.join(File.separator, paths) + File.separator + filename;
    }

    @Override
    public String resolveFileUrl(String filePath) {
        filePath = filePath.replace(File.separator, "/");
        return this.serverOrigin + filePath;
    }
}
