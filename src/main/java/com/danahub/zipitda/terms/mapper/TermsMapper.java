package com.danahub.zipitda.terms.mapper;
import com.danahub.zipitda.terms.dto.TermsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
//@Repository("termsMapperBean")
public interface TermsMapper {
    List<TermsResponseDto> getTermsList();
}
