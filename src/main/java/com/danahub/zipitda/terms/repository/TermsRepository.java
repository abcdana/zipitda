package com.danahub.zipitda.terms.repository;

import com.danahub.zipitda.terms.domain.Terms;
import com.danahub.zipitda.terms.domain.TermsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsRepository extends JpaRepository<Terms, TermsId> {

    @Query("SELECT t " +
            "FROM Terms t " +
            "WHERE t.id.version = (" +
            "    SELECT MAX(t2.id.version) " +
            "    FROM Terms t2 " +
            "    WHERE t2.id.title = t.id.title" +
            ")")
    List<Terms> findLatestTermsByTitle();
}