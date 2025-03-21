package com.life.master_api.repositories;

import com.life.master_api.entities.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {
}