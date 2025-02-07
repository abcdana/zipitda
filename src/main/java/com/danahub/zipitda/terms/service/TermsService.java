package com.danahub.zipitda.terms.service;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.domain.TermsId;
import com.danahub.zipitda.terms.dto.TermsResponseDto;
import com.danahub.zipitda.terms.mapper.TermsMapper;
import com.danahub.zipitda.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {

    private final TermsMapper termsMapper;
    private final TermsRepository termsRepository;


    // 약관 리스트 가져오기
    public List<TermsResponseDto> getTermsList() {
        List<TermsResponseDto> termsList = termsMapper.getTermsList();
        return termsList;
    }

    // 특정 약관 가져오기 (복합키)
    public Terms getTermsByTitleAndVersion(String title, Integer version) {
        TermsId termsId = new TermsId(title, version);
        return termsRepository.findById(termsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약관을 찾을 수 없습니다."));
    }

}