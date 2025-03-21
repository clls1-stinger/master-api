package com.life.master_api.controllers;

import com.life.master_api.entities.Task;
<<<<<<< Updated upstream
=======
import com.life.master_api.entities.TaskHistory;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskHistoryRepository;
>>>>>>> Stashed changes
import com.life.master_api.repositories.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
<<<<<<< Updated upstream

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
=======
    private final TaskHistoryRepository taskHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public TaskController(TaskRepository taskRepository,
                          TaskHistoryRepository taskHistoryRepository,
                          CategoryRepository categoryRepository,
                          NoteRepository noteRepository,
                          HabitRepository habitRepository) {
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
>>>>>>> Stashed changes
    }

    @Operation(summary = "Obtener todas las tareas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida exitosamente")
    // GET /tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @Operation(summary = "Crear una nueva tarea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // POST /tasks
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
    // GET /tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@Parameter(description = "ID de la tarea a obtener") @PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una tarea existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // PUT /tasks/{id}
    @PutMapping("/{id}")
<<<<<<< Updated upstream
    public ResponseEntity<Task> updateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, @Valid @RequestBody Task taskDetails) {
=======
    public ResponseEntity<Task> updateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id,
                                           @Valid @RequestBody Task taskDetails) {
>>>>>>> Stashed changes
        return taskRepository.findById(id)
                .map(existingTask -> {
                    // Guarda el historial antes de actualizar
                    saveTaskHistory(existingTask);

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
    // PATCH /tasks/{id}
    @PatchMapping("/{id}")
<<<<<<< Updated upstream
    public ResponseEntity<Task> partialUpdateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, @RequestBody Task taskDetails) {
=======
    public ResponseEntity<Task> patchTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id,
                                          @RequestBody Task taskDetails) {
>>>>>>> Stashed changes
        return taskRepository.findById(id)
                .map(existingTask -> {
                    // Guarda el historial antes de actualizar
                    saveTaskHistory(existingTask);

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
    // DELETE /tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID de la tarea a eliminar") @PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
<<<<<<< Updated upstream
=======

    // Endpoints para historial de tareas

    @Operation(summary = "Obtener el historial de versiones de una tarea")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@Parameter(description = "ID de la tarea") @PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    List<TaskHistory> historyList = new ArrayList<>(task.getHistory());
                    historyList.sort(Comparator.comparing(TaskHistory::getModificationTimestamp).reversed()); // Ordenar por fecha descendente
                    return ResponseEntity.ok(historyList);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener una versión específica del historial de tarea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Versión del historial de tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Versión del historial de tarea no encontrada")
    })
    @GetMapping("/{id}/history/{historyId}")
    public ResponseEntity<TaskHistory> getTaskHistoryVersion(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @Parameter(description = "ID del historial de la tarea") @PathVariable Long historyId) {
        return taskHistoryRepository.findById(historyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Métodos auxiliares

    private void saveTaskHistory(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTaskId(task.getId());
        history.setTitle(task.getTitle());
        history.setDescription(task.getDescription());
        history.setCreation(task.getCreation());
        history.setModificationTimestamp(new Date()); // Fecha actual como timestamp de modificación
        taskHistoryRepository.save(history);
    }


    // Endpoints para gestionar relaciones (sin cambios en esta parte por ahora)

    @Operation(summary = "Obtener todas las categorías de una tarea")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Set<Category>> getTaskCategories(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una categoría a una tarea")
    @PostMapping("/{taskId}/categories/{categoryId}")
    public ResponseEntity<Void> addTaskToCategory(@PathVariable Long taskId, @PathVariable Long categoryId) {
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
>>>>>>> Stashed changes
}