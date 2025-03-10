package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // 파일 저장
    public String uploadFile(MultipartFile file) {
        validateFile(file);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destinationFile = Paths.get(UPLOAD_DIR, fileName).toFile();

        try {
            file.transferTo(destinationFile);
        } catch (Exception e) {
            throw new ZipitdaException(ErrorType.INTERNAL_SERVER_ERROR,
                                        Map.of("fileName", fileName, "originalFileName", file.getOriginalFilename())
                                        ,log::warn,
                                        e);
        }

        return "/uploads/" + fileName; // 저장된 파일의 URL 반환
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String filePath = UPLOAD_DIR + fileUrl.replace("/uploads/", "");
        File file = new File(filePath);

        if (file.exists() && file.delete()) {
            log.info("파일 삭제 완료: " + filePath);
        } else {
            throw new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND, Map.of("filePath", filePath));
        }
    }

    // 파일 검증 (확장자 체크)
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ZipitdaException(ErrorType.INVALID_REQUEST);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            throw new ZipitdaException(ErrorType.UNSUPPORTED_REQUEST_TYPE, Map.of("fileName", fileName));
        }
    }
}