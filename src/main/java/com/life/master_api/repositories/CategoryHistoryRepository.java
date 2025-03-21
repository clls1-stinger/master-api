package com.life.master_api.repositories;

import com.life.master_api.entities.CategoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {

    List<CategoryHistory> findByCategory_IdOrderByVersionIdDesc(Long categoryId);
    List<CategoryHistory> findByCategory_IdAndVersionId(Long categoryId, Long versionId);
}