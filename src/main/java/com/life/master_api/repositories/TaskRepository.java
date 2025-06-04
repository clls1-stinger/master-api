package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    Page<Task> findAll(Pageable pageable);
    
    List<Task> findByUser(User user);
    
    Page<Task> findByUser(User user, Pageable pageable);
    
    Optional<Task> findByIdAndUser(Long id, User user);
    
    // Los métodos findByCategory y findByCategoryAndUser ahora son implementados por TaskRepositoryCustomImpl
    // usando Criteria API para mayor seguridad
    
    Page<Task> findByUserAndTitleContains(User user, String title, Pageable pageable);
}