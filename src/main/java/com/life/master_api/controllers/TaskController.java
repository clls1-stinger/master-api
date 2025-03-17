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
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public TaskController(TaskRepository taskRepository, 
                         CategoryRepository categoryRepository, 
                         NoteRepository noteRepository, 
                         HabitRepository habitRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener todas las tareas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @Operation(summary = "Crear una nueva tarea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        task.setCreation(new Date());
        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una tarea por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@Parameter(description = "ID de la tarea a obtener") @PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una tarea existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, 
                                          @Valid @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente una tarea existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Task> partialUpdateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, 
                                                 @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    if (taskDetails.getTitle() != null) {
                        existingTask.setTitle(taskDetails.getTitle());
                    }
                    if (taskDetails.getDescription() != null) {
                        existingTask.setDescription(taskDetails.getDescription());
                    }
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una tarea por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID de la tarea a eliminar") @PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las categorías de una tarea")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Set<Category>> getTaskCategories(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una categoría a una tarea")
    @PostMapping("/{taskId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToTask(@PathVariable Long taskId, @PathVariable Long categoryId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        
        if (taskOpt.isPresent() && categoryOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().add(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de una tarea")
    @DeleteMapping("/{taskId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromTask(@PathVariable Long taskId, @PathVariable Long categoryId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        
        if (taskOpt.isPresent() && categoryOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().remove(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las notas relacionadas con una tarea")
    @GetMapping("/{id}/notes")
    public ResponseEntity<Set<Note>> getTaskNotes(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todos los hábitos relacionados con una tarea")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getTaskHabits(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}