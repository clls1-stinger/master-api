package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/notes/{noteId}/categories")
public class NoteCategoryController {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;

    public NoteCategoryController(NoteRepository noteRepository, CategoryRepository categoryRepository) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
    }

    // GET /notes/{noteId}/categories
    @GetMapping
    public ResponseEntity<Set<Category>> getCategoriesForNote(@PathVariable Long noteId) {
        return noteRepository.findById(noteId)
                .map(note -> ResponseEntity.ok(note.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /notes/{noteId}/categories/{categoryId}
    @PostMapping("/{categoryId}")
    public ResponseEntity<Void> addCategoryToNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        return noteRepository.findById(noteId)
                .flatMap(note -> categoryRepository.findById(categoryId)
                        .map(category -> {
                            note.getCategories().add(category);
                            noteRepository.save(note);
                            return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /notes/{noteId}/categories/{categoryId}
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        return noteRepository.findById(noteId)
                .flatMap(note -> categoryRepository.findById(categoryId)
                        .map(category -> {
                            note.getCategories().remove(category);
                            noteRepository.save(note);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}