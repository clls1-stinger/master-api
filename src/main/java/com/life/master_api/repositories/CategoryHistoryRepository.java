package com.life.master_api.repositories;

import com.life.master_api.entities.CategoryHistory;
import com.life.master_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryHistoryRepository extends JpaRepository<CategoryHistory, Long> {

    List<CategoryHistory> findByCategory_IdOrderByVersionIdDesc(Long categoryId);
    List<CategoryHistory> findByCategory_IdAndVersionId(Long categoryId, Long versionId);
    
    // User-based filtering methods for data isolation
    List<CategoryHistory> findByCategory_IdAndUserOrderByVersionIdDesc(Long categoryId, User user);
    List<CategoryHistory> findByCategory_IdAndVersionIdAndUser(Long categoryId, Long versionId, User user);
}