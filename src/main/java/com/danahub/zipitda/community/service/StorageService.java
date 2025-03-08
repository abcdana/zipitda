package com.danahub.zipitda.community.service;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
                                        Map.of("fileName", fileName, "originalFileName", file.getOriginalFilename()),
                                        log -> System.err.println(log),
                                        e);
        }

        return "/uploads/" + fileName; // 저장된 파일의 URL 반환
    }

    // 파일 삭제
    public void deleteFile(String fileUrl) {
        String filePath = UPLOAD_DIR + fileUrl.replace("/uploads/", "");
        File file = new File(filePath);

        if (file.exists() && file.delete()) {
            System.out.println("파일 삭제 완료: " + filePath);
        } else {
            throw new ZipitdaException(ErrorType.RESOURCE_NOT_FOUND, Map.of("message", "삭제할 파일이 존재하지 않습니다."));
        }
    }

    // 파일 검증 (확장자 체크)
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ZipitdaException(ErrorType.INVALID_REQUEST, Map.of("message", "업로드할 파일이 없습니다."));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            throw new ZipitdaException(ErrorType.UNSUPPORTED_REQUEST_TYPE, Map.of("message", "지원하지 않는 파일 형식입니다."));
        }
    }
}