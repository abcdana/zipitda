package com.danahub.zipitda.terms.service;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.repository.TermsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TermsService {

    private final TermsRepository termsRepository;

    public TermsService(TermsRepository termsRepository) {
        this.termsRepository = termsRepository;
    }

    public List<Terms> getAllTerms() {
        return termsRepository.findAll();
    }

    public Terms getTermsById(Integer id) {
        return termsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 약관이 존재하지 않습니다. ID: " + id));
    }
}