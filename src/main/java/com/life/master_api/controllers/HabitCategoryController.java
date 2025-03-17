package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/habits/{habitId}/categories")
public class HabitCategoryController {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;

    public HabitCategoryController(HabitRepository habitRepository, CategoryRepository categoryRepository) {
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
    }

    // GET /habits/{habitId}/categories
    @GetMapping
    public ResponseEntity<Set<Category>> getCategoriesForHabit(@PathVariable Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> ResponseEntity.ok(habit.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /habits/{habitId}/categories/{categoryId}
    @PostMapping("/{categoryId}")
    public ResponseEntity<Void> addCategoryToHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> categoryRepository.findById(categoryId)
                        .map(category -> {
                            habit.getCategories().add(category);
                            habitRepository.save(habit);
                            return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /habits/{habitId}/categories/{categoryId}
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> categoryRepository.findById(categoryId)
                        .map(category -> {
                            habit.getCategories().remove(category);
                            habitRepository.save(habit);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}