package com.danahub.zipitda.store.repository;

import com.danahub.zipitda.store.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
