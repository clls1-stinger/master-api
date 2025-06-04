package com.life.master_api.repositories;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteRepositoryCustom {
    List<Note> findByCategory(Category category);
    Page<Note> findByCategoryAndUser(Category category, User user, Pageable pageable);
}