package com.danahub.zipitda.terms.service;

import com.danahub.zipitda.terms.controller.TermsController;
import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.domain.TermsId;
import com.danahub.zipitda.terms.dto.TermsResponseDto;
import com.danahub.zipitda.terms.mapper.TermsMapper;
import com.danahub.zipitda.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermsService {

    private static final Logger logger = LoggerFactory.getLogger(TermsController.class);

    private final TermsMapper termsMapper;
    private final TermsRepository termsRepository;


    // 약관 리스트 가져오기
    public List<TermsResponseDto> getTermsList() {
        logger.info("[TermsService] getTermsList - 약관 리스트 조회 시작");
        List<TermsResponseDto> termsList = termsMapper.getTermsList();
        logger.info("[TermsService] 약관 {}건 조회 성공", termsList.size());
        return termsList;
    }

    // 특정 약관 가져오기 (복합키)
    public Terms getTermsByTitleAndVersion(String title, Integer version) {
        TermsId termsId = new TermsId(title, version);
        return termsRepository.findById(termsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약관을 찾을 수 없습니다."));
    }

}