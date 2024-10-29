package com.hf.healthfriend.global.file.local;

import com.hf.healthfriend.global.file.FileUrlResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 따로 Profile이 설정되지 않으면 Spring Bean으로 등록된다.
 *
 * @author PGD
 * @see com.hf.healthfriend.global.config.BeanConfig
 */
@Slf4j
@RequiredArgsConstructor
public class LocalFileUrlResolver implements FileUrlResolver {
    private static final String FILE_UPLOAD_URL_BASE = "/hr/files";

    private final String serverOrigin;

    @Override
    public String generateFilePath(String filename, String... paths) {
        return String.join("/", paths) + (paths.length > 0 ? "/" : "") + filename;
    }

    @Override
    public String generateFilePathWithUuid(String... paths) {
        return generateFilePath(UUID.randomUUID().toString(), paths);
    }

    @Override
    public String resolveFileUrl(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }
        return this.serverOrigin + (filePath.startsWith("/") ? filePath : "/" + filePath);
    }

    @Override
    public String generateUploadUrl(String filename, String... paths) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        return this.serverOrigin +
                FILE_UPLOAD_URL_BASE +
                (paths.length > 0 ? "/" : "") +
                String.join("/", paths) +
                (filename.startsWith("/") ? "" : "/") +
                filename;
    }
}
