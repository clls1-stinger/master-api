package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public CategoryController(CategoryRepository categoryRepository, TaskRepository taskRepository, NoteRepository noteRepository, HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener todas las categorías")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    // GET /categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @Operation(summary = "Obtener una categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    // GET /categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "ID de la categoría a obtener") @PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Buscar categorías por nombre (parcial)")
    @ApiResponse(responseCode = "200", description = "Lista de categorías que coinciden con el nombre")
    // GET /categories/search/by-name
    @GetMapping("/search/by-name")
    public ResponseEntity<List<Category>> getCategoriesByName(@Parameter(description = "Nombre a buscar en categorías") @RequestParam String name) {
        System.out.println("Buscando categorías por nombre: " + name);
        return ResponseEntity.ok(categoryRepository.findByNameContains(name));
    }

    @Operation(summary = "Crear una nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // POST /categories
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category,
                                                   @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                   @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                   @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        category.setCreation(new Date());

        if (taskIds != null && !taskIds.isEmpty()) {
            Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
            category.setTasks(tasks);
        }
        if (noteIds != null && !noteIds.isEmpty()) {
            Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
            category.setNotes(notes);
        }
        if (habitIds != null && !habitIds.isEmpty()) {
            Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
            category.setHabits(habits);
        }

        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una categoría existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // PUT /categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @Valid @RequestBody Category categoryDetails,
                                                   @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                   @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                   @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDetails.getName());
                    existingCategory.setDescription(categoryDetails.getDescription());

                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingCategory.setTasks(tasks);
                    } else {
                        existingCategory.getTasks().clear();
                    }
                    if (noteIds != null && !noteIds.isEmpty()) {
                        Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
                        existingCategory.setNotes(notes);
                    } else {
                        existingCategory.getNotes().clear();
                    }
                    if (habitIds != null && !habitIds.isEmpty()) {
                        Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
                        existingCategory.setHabits(habits);
                    } else {
                        existingCategory.getHabits().clear();
                    }

                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente una categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    // PATCH /categories/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Category> patchCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @RequestBody Category categoryDetails,
                                                  @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                  @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                  @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    if (categoryDetails.getName() != null) {
                        existingCategory.setName(categoryDetails.getName());
                    }
                    if (categoryDetails.getDescription() != null) {
                        existingCategory.setDescription(categoryDetails.getDescription());
                    }

                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingCategory.setTasks(tasks);
                    }
                    if (noteIds != null && !noteIds.isEmpty()) {
                        Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
                        existingCategory.setNotes(notes);
                    }
                    if (habitIds != null && !habitIds.isEmpty()) {
                        Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
                        existingCategory.setHabits(habits);
                    }

                    Category patchedCategory = categoryRepository.save(existingCategory);
                    return ResponseEntity.ok(patchedCategory);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    // DELETE /categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "ID de la categoría a eliminar") @PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}