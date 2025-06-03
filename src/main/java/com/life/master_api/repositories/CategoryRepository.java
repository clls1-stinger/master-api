package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // <-- ¡IMPORTA List!

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameContains(String name);
    
    Page<Category> findByNameContains(String name, Pageable pageable);
    
    Page<Category> findAll(Pageable pageable);
}