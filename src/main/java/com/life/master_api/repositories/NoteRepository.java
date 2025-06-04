package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long>, NoteRepositoryCustom {
    Page<Note> findAll(Pageable pageable);
    
    List<Note> findByUser(User user);
    
    Page<Note> findByUser(User user, Pageable pageable);
    
    Optional<Note> findByIdAndUser(Long id, User user);
    
    // Los métodos findByCategory y findByCategoryAndUser ahora son implementados por NoteRepositoryCustomImpl
    // usando Criteria API para mayor seguridad
    
    Page<Note> findByUserAndTitleContains(User user, String title, Pageable pageable);
    
    Page<Note> findByUserAndNoteContains(User user, String content, Pageable pageable);
}