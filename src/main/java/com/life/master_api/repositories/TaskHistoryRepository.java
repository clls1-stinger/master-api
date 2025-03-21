package com.life.master_api.repositories;

import com.life.master_api.entities.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {

    List<TaskHistory> findByTask_IdOrderByVersionIdDesc(Long taskId);
    List<TaskHistory> findByTask_IdAndVersionId(Long taskId, Long versionId);
}