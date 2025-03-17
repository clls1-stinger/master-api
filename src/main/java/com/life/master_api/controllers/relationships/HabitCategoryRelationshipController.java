package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.relationships.HabitCategoryRelationship;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.relationships.HabitCategoryRelationshipRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/habit-categories")
public class HabitCategoryRelationshipController {

    private final HabitCategoryRelationshipRepository habitCategoryRelationshipRepository;
    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;

    public HabitCategoryRelationshipController(HabitCategoryRelationshipRepository habitCategoryRelationshipRepository, HabitRepository habitRepository, CategoryRepository categoryRepository) {
        this.habitCategoryRelationshipRepository = habitCategoryRelationshipRepository;
        this.habitRepository = habitRepository;
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Obtener todas las relaciones HabitCategory")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones HabitCategory obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<HabitCategoryRelationship>> getAllHabitCategoryRelationships() {
        return ResponseEntity.ok(habitCategoryRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación HabitCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación HabitCategory creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<HabitCategoryRelationship> createHabitCategoryRelationship(@RequestParam Long habitId, @RequestParam Long categoryId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        Optional<Category> category = categoryRepository.findById(categoryId);

        if (habit.isPresent() && category.isPresent()) {
            HabitCategoryRelationship relationship = new HabitCategoryRelationship(habit.get(), category.get());
            HabitCategoryRelationship savedRelationship = habitCategoryRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitCategory por ID de hábito")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitCategory encontradas por Habit ID")
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitCategoryRelationship>> getHabitCategoryRelationshipsByHabitId(@PathVariable Long habitId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            List<HabitCategoryRelationship> relationships = habitCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getHabit().getId().equals(habitId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitCategory por ID de categoría")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitCategory encontradas por Category ID")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<HabitCategoryRelationship>> getHabitCategoryRelationshipsByCategoryId(@PathVariable Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            List<HabitCategoryRelationship> relationships = habitCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una relación HabitCategory por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación HabitCategory eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación HabitCategory no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitCategoryRelationship(@PathVariable Long id) {
        return habitCategoryRelationshipRepository.findById(id)
                .map(relationship -> {
                    habitCategoryRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}