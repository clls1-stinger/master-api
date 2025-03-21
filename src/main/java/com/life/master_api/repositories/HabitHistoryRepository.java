package com.life.master_api.repositories;

import com.life.master_api.entities.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {

    List<HabitHistory> findByHabit_IdOrderByVersionIdDesc(Long habitId);
    List<HabitHistory> findByHabit_IdAndVersionId(Long habitId, Long versionId);
}