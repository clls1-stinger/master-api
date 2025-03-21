package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.CategoryHistory;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.repositories.CategoryHistoryRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryHistoryRepository categoryHistoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public CategoryController(CategoryRepository categoryRepository,
                                CategoryHistoryRepository categoryHistoryRepository,
                                TaskRepository taskRepository,
                                NoteRepository noteRepository,
                                HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryHistoryRepository = categoryHistoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener todas las categorías")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @Operation(summary = "Obtener una categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "ID de la categoría a obtener") @PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar categorías por nombre (parcial)")
    @ApiResponse(responseCode = "200", description = "Lista de categorías que coinciden con el nombre")
    @GetMapping("/search/by-name")
    public ResponseEntity<List<Category>> getCategoriesByName(@Parameter(description = "Nombre a buscar en categorías") @RequestParam String name) {
        return ResponseEntity.ok(categoryRepository.findByNameContains(name));
    }

    @Operation(summary = "Crear una nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        category.setCreation(new Date());
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una categoría existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id,
                                                  @Valid @RequestBody Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Save history before update
                    saveCategoryHistory(existingCategory);

                    existingCategory.setName(categoryDetails.getName());
                    existingCategory.setDescription(categoryDetails.getDescription());
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
    @PatchMapping("/{id}")
    public ResponseEntity<Category> patchCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id,
                                                 @RequestBody Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Save history before update
                    saveCategoryHistory(existingCategory);

                    if (categoryDetails.getName() != null) {
                        existingCategory.setName(categoryDetails.getName());
                    }
                    if (categoryDetails.getDescription() != null) {
                        existingCategory.setDescription(categoryDetails.getDescription());
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "ID de la categoría a eliminar") @PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las tareas de una categoría")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Set<Task>> getCategoryTasks(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(category.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una tarea a una categoría")
    @PostMapping("/{categoryId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToCategory(@PathVariable Long categoryId, @PathVariable Long taskId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (categoryOpt.isPresent() && taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().add(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una tarea de una categoría")
    @DeleteMapping("/{categoryId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromCategory(@PathVariable Long categoryId, @PathVariable Long taskId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (categoryOpt.isPresent() && taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().remove(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las notas de una categoría")
    @GetMapping("/{id}/notes")
    public ResponseEntity<Set<Note>> getCategoryNotes(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(category.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una nota a una categoría")
    @PostMapping("/{categoryId}/notes/{noteId}")
    public ResponseEntity<Void> addNoteToCategory(@PathVariable Long categoryId, @PathVariable Long noteId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Note> noteOpt = noteRepository.findById(noteId);

        if (categoryOpt.isPresent() && noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().add(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una nota de una categoría")
    @DeleteMapping("/{categoryId}/notes/{noteId}")
    public ResponseEntity<Void> removeNoteFromCategory(@PathVariable Long categoryId, @PathVariable Long noteId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Note> noteOpt = noteRepository.findById(noteId);

        if (categoryOpt.isPresent() && noteOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().remove(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todos los hábitos de una categoría")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getCategoryHabits(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(category.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar un hábito a una categoría")
    @PostMapping("/{categoryId}/habits/{habitId}")
    public ResponseEntity<Void> addHabitToCategory(@PathVariable Long categoryId, @PathVariable Long habitId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Habit> habitOpt = habitRepository.findById(habitId);

        if (categoryOpt.isPresent() && habitOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().add(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un hábito de una categoría")
    @DeleteMapping("/{categoryId}/habits/{habitId}")
    public ResponseEntity<Void> removeHabitFromCategory(@PathVariable Long categoryId, @PathVariable Long habitId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        Optional<Habit> habitOpt = habitRepository.findById(habitId);

        if (categoryOpt.isPresent() && habitOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().remove(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // History Endpoints

    @Operation(summary = "Obtener el historial de versiones de una categoría")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<CategoryHistory>> getCategoryHistory(@Parameter(description = "ID de la categoría") @PathVariable Long id) {
        List<CategoryHistory> history = categoryHistoryRepository.findByCategory_IdOrderByVersionIdDesc(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener una versión específica del historial de una categoría")
    @GetMapping("/{id}/history/{versionId}")
    public ResponseEntity<CategoryHistory> getCategoryHistoryByVersion(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Parameter(description = "ID de la versión del historial") @PathVariable Long versionId) {
        List<CategoryHistory> historyVersion = categoryHistoryRepository.findByCategory_IdAndVersionId(id, versionId);
        if (!historyVersion.isEmpty()) {
            return ResponseEntity.ok(historyVersion.get(0)); // Assuming versionId is unique for each category
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private void saveCategoryHistory(Category category) {
        CategoryHistory history = new CategoryHistory();
        history.setCategory(category);
        history.setName(category.getName());
        history.setDescription(category.getDescription());
        history.setCreation(category.getCreation());
        history.setTimestamp(new Date()); // Current timestamp
        // You might want to implement a more robust versioning logic here, e.g., incrementing versionId
        // For simplicity, we are not setting versionId for now, you can add a version counter if needed.

        categoryHistoryRepository.save(history);
    }
}