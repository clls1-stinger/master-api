package com.life.master_api.repositories;

import com.life.master_api.entities.CategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {
}