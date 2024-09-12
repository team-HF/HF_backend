package com.hf.healthfriend.global.util.file.local;

import com.hf.healthfriend.global.util.file.FileUrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public String generateFilePath(String filename, String... paths) {
        // TODO: 하드코딩 제거해야 함
        return "/files/" + String.join("/", paths) + "/" + filename;
    }

    @Override
    public String resolveFileUrl(String filePath) {
        return this.serverOrigin + filePath;
    }
}
