package com.hf.healthfriend.global.util.file;

/**
 * 파일의 정확한 위치를 나타내는 URL을 생성해 주는 interface.
 * 로컬 (개발) 환경에 파일을 저장하는지, 클라우드에 저장하는지
 * Windows에서 저장하는지 Linux에서 저장하는지
 * 여러 상황에 따라 다양한 구현체가 있을 수 있다.
 * Spring Profile에 따라 구현체를 선택된다.
 * @author PGD
 */
public interface FileUrlResolver {

    /**
     * filename에 대해서 저장할 파일의 경로를 생성해 주는 메소드
     * @param filename 저장할 파일명
     * @param paths 저장할 파일의 경로
     * @return 파일이 저장될 경로 + 파일명 e.g. /image/my-profile.jpg
     */
    String generateFilePath(String filename, String... paths);

    /**
     * filePath로부터 웹에서 접근할 수 있는 URL을 생성해 준다.
     * @param filePath 파일이 저장된 경로. "/"으로 시작해야 하며 파일명까지 붙여줘야 한다. e.g. /image/my-profile.jpg
     * @return 파일에 접근할 수 있는 URL 경로
     */
    String resolveFileUrl(String filePath);
}
