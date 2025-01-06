package com.danahub.zipitda.terms.controller;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.domain.TermsId;
import com.danahub.zipitda.terms.dto.TermsResponseDto;
import com.danahub.zipitda.terms.service.TermsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
public class TermsController {

    private static final Logger logger = LoggerFactory.getLogger(TermsController.class);

    private final TermsService termsService;

    public TermsController(TermsService termsService) {
        this.termsService = termsService;
    }

    // 약관 리스트 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<TermsResponseDto>> getTermsList() {
        logger.info("[TermsController] /api/terms 요청 수신 - 약관 리스트 조회 시작");
        List<TermsResponseDto> termsList = termsService.getTermsList();

        if (termsList.isEmpty()) {
            logger.warn("[TermsController] 약관 데이터가 없습니다.");
            return ResponseEntity.noContent().build(); // 204 No Content 반환
        }

        logger.info("[TermsController] 약관 리스트 응답 성공 - 총 {}건 반환", termsList.size());
        return ResponseEntity.ok(termsList); // 200 OK 반환
    }

    // 특정 약관 가져오기 (복합키)
    @GetMapping("/{title}/{version}")
    public ResponseEntity<Terms> getTermsByTitleAndVersion(
            @PathVariable String title,
            @PathVariable Integer version) {
        logger.info("[TermsController] /api/terms/{}/{} 요청 수신 - 특정 약관 조회 시작", title, version);
        Terms terms = termsService.getTermsByTitleAndVersion(title, version);

        logger.info("[TermsController] 특정 약관 조회 성공 - title: {}, version: {}", title, version);
        return ResponseEntity.ok(terms); // 200 OK 반환
    }

}