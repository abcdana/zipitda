package com.danahub.zipitda.terms.repository;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.domain.TermsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, TermsId> {
}