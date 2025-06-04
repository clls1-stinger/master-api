package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameContains(String name);
    
    Page<Category> findByNameContains(String name, Pageable pageable);
    
    Page<Category> findAll(Pageable pageable);
    
    List<Category> findByUser(User user);
    
    Page<Category> findByUser(User user, Pageable pageable);
    
    Optional<Category> findByIdAndUser(Long id, User user);
    
    Page<Category> findByUserAndNameContains(User user, String name, Pageable pageable);
}