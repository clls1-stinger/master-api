package com.life.master_api.repositories;

import com.life.master_api.entities.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}