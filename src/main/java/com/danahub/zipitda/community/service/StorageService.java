package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    // JAR 외부 디렉토리에 저장하기 위해 프로젝트 루트 디렉토리에 uploads 폴더 사용
    private static final String UPLOAD_DIR = "uploads/";

    // 파일 저장
    public String uploadFile(MultipartFile file) {
        validateFile(file);

        // 업로드 폴더 존재 여부 확인 후 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                log.info(" 업로드 디렉터리 생성: {}", uploadDir.getAbsolutePath());
            } else {
                throw new ZipitdaException(ErrorType.INTERNAL_SERVER_ERROR, Map.of("message", "업로드 디렉터리 생성 실패"));
            }
        }

        // 파일 확장자 유지 + UUID 파일명 적용 (한글 파일명 방지)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
        String safeFileName = UUID.randomUUID().toString() + fileExtension; // 한글 파일명 제거
        File destinationFile = new File(uploadDir, safeFileName);

        // 파일 저장 (transferTo -> FileOutputStream)
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(file.getBytes());
            log.info("파일 업로드 성공: {}", safeFileName);
        } catch (IOException e) {
            throw new ZipitdaException(ErrorType.INTERNAL_SERVER_ERROR,
                    Map.of("fileName", safeFileName, "originalFileName", originalFilename), log::warn, e);
        }

        //  암호화된 URL 반환
        return encryptUrl("/uploads/" + safeFileName);
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String filePath = UPLOAD_DIR + fileUrl.replace("/uploads/", "");
        File file = new File(filePath);

        if (file.exists() && file.delete()) {
            log.info("파일 삭제 완료: {}", filePath);
        } else {
            throw new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND, Map.of("filePath", filePath));
        }
    }

    // 파일 검증 (확장자 체크)
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ZipitdaException(ErrorType.INVALID_REQUEST, Map.of("message", "파일이 비어 있습니다."));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            throw new ZipitdaException(ErrorType.UNSUPPORTED_REQUEST_TYPE, Map.of("fileName", fileName, "message", "지원하지 않는 파일 형식입니다."));
        }
    }

    // 파일 URL 암호화
    private String encryptUrl(String url) {
        return Base64.getEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
    }
}