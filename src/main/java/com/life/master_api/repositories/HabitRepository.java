package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long>, HabitRepositoryCustom {
    Page<Habit> findAll(Pageable pageable);
    
    List<Habit> findByUser(User user);
    
    Page<Habit> findByUser(User user, Pageable pageable);
    
    Optional<Habit> findByIdAndUser(Long id, User user);
    
    // El método findByCategories ahora es implementado por HabitRepositoryCustomImpl
    // usando Criteria API para mayor seguridad
    
    Page<Habit> findByUserAndNameContains(User user, String name, Pageable pageable);
    
    // Métodos para seguimiento diario de hábitos
    List<Habit> findByActiveTrue();
    
    List<Habit> findByUserAndActiveTrue(User user);
    
    Page<Habit> findByUserAndActiveTrue(User user, Pageable pageable);
    
    Page<Habit> findByUserAndActiveFalse(User user, Pageable pageable);
}