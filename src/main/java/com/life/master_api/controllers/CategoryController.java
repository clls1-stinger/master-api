package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.CategoryHistory;
import com.life.master_api.repositories.CategoryHistoryRepository;
import com.life.master_api.repositories.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.Comparator;
>>>>>>> Stashed changes
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryHistoryRepository categoryHistoryRepository; // Inyecta el nuevo repositorio de historial

<<<<<<< Updated upstream
    public CategoryController(CategoryRepository categoryRepository, TaskRepository taskRepository, NoteRepository noteRepository, HabitRepository habitRepository) {
=======
    public CategoryController(CategoryRepository categoryRepository, CategoryHistoryRepository categoryHistoryRepository) {
>>>>>>> Stashed changes
        this.categoryRepository = categoryRepository;
        this.categoryHistoryRepository = categoryHistoryRepository;
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
<<<<<<< Updated upstream
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @Valid @RequestBody Category categoryDetails,
                                                   @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                   @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                   @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
=======
    public ResponseEntity<Category> updateCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
>>>>>>> Stashed changes
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Guarda el historial antes de actualizar
                    saveCategoryHistory(existingCategory);

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
<<<<<<< Updated upstream
    public ResponseEntity<Category> patchCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @RequestBody Category categoryDetails,
                                                  @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                  @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                  @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
=======
    public ResponseEntity<Category> patchCategory(@Parameter(description = "ID de la categoría a actualizar") @PathVariable Long id, @RequestBody Category categoryDetails) {
>>>>>>> Stashed changes
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Guarda el historial antes de actualizar
                    saveCategoryHistory(existingCategory);

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
<<<<<<< Updated upstream
=======

    // Endpoints para historial de categorías

    @Operation(summary = "Obtener el historial de versiones de una categoría")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<CategoryHistory>> getCategoryHistory(@Parameter(description = "ID de la categoría") @PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    List<CategoryHistory> historyList = new ArrayList<>(category.getHistory());
                    historyList.sort(Comparator.comparing(CategoryHistory::getModificationTimestamp).reversed()); // Ordenar por fecha descendente
                    return ResponseEntity.ok(historyList);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener una versión específica del historial de categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Versión del historial de categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Versión del historial de categoría no encontrada")
    })
    @GetMapping("/{id}/history/{historyId}")
    public ResponseEntity<CategoryHistory> getCategoryHistoryVersion(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Parameter(description = "ID del historial de la categoría") @PathVariable Long historyId) {
        return categoryHistoryRepository.findById(historyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Métodos auxiliares

    private void saveCategoryHistory(Category category) {
        CategoryHistory history = new CategoryHistory();
        history.setCategoryId(category.getId());
        history.setName(category.getName());
        history.setDescription(category.getDescription());
        history.setCreation(category.getCreation());
        history.setModificationTimestamp(new Date()); // Fecha actual como timestamp de modificación
        categoryHistoryRepository.save(history);
    }


    // Endpoints para gestionar relaciones (sin cambios en esta parte por ahora)

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

    @Operation(summary = "Eliminar una categoría de una tarea")
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

        if (habitOpt.isPresent() && categoryOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().remove(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
>>>>>>> Stashed changes
}