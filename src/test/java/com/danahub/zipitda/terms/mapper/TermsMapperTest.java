package com.danahub.zipitda.terms.mapper;

import com.danahub.zipitda.terms.dto.TermsResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TermsMapperTest {

    @Autowired
    private TermsMapper termsMapper;

    @Test
    public void testGetTermsList() {
        // when
        List<TermsResponseDto> termsList = termsMapper.getTermsList();

        // then
        Assertions.assertNotNull(termsList, "The result should not be null");
        Assertions.assertFalse(termsList.isEmpty(), "The terms list should not be empty");

        // Optional: 출력 결과 확인
        termsList.forEach(terms -> System.out.println(terms));
    }
}