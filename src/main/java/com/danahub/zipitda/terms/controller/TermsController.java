package com.danahub.zipitda.terms.controller;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.service.TermsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
public class TermsController {

    private final TermsService termsService;

    public TermsController(TermsService termsService) {
        this.termsService = termsService;
    }

    // 약관 목록 가져오기
    @GetMapping
    public ResponseEntity<List<Terms>> getAllTerms() {
        return ResponseEntity.ok(termsService.getAllTerms());
    }

    // 특정 약관 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<Terms> getTermsById(@PathVariable Integer id) {
        return ResponseEntity.ok(termsService.getTermsById(id));
    }
}