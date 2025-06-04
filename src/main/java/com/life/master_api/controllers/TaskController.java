package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.TaskHistory;
import com.life.master_api.entities.User;
import com.life.master_api.exceptions.ResourceNotFoundException;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskHistoryRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "API para gestión de tareas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskRepository taskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository,
                            TaskHistoryRepository taskHistoryRepository,
                            CategoryRepository categoryRepository,
                            NoteRepository noteRepository,
                            HabitRepository habitRepository,
                            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskHistoryRepository = taskHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }
    
    // Método para obtener el usuario autenticado actual
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));
    }

    @Operation(summary = "Obtener todas las tareas del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findByUser(currentUser, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", taskPage.getContent());
        response.put("currentPage", taskPage.getNumber());
        response.put("totalItems", taskPage.getTotalElements());
        response.put("totalPages", taskPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva tarea para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        task.setUser(currentUser);
        task.setCreation(new Date());
        Task savedTask = taskRepository.save(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una tarea por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@Parameter(description = "ID de la tarea a obtener") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una tarea existente del usuario autenticado (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id,
                                          @Valid @RequestBody Task taskDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(existingTask -> {
                    // Save history before update
                    saveTaskHistory(existingTask);

                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    // Mantener el usuario actual
                    existingTask.setUser(currentUser);
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente una tarea existente del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Task> partialUpdateTask(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id,
                                                 @RequestBody Task taskDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(existingTask -> {
                    // Save history before update
                    saveTaskHistory(existingTask);

                    if (taskDetails.getTitle() != null) {
                        existingTask.setTitle(taskDetails.getTitle());
                    }
                    if (taskDetails.getDescription() != null) {
                        existingTask.setDescription(taskDetails.getDescription());
                    }
                    // Mantener el usuario actual
                    existingTask.setUser(currentUser);
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una tarea por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada o no pertenece al usuario")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID de la tarea a eliminar") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(task -> {
                    taskRepository.delete(task);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las categorías de una tarea del usuario autenticado")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Map<String, Object>> getTaskCategories(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(id, currentUser);
        if (!taskOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Task task = taskOpt.get();
        List<Category> categories = new ArrayList<>(task.getCategories());
        
        // Aplicar ordenamiento
        Comparator<Category> comparator;
        if (sortBy.equals("id")) {
            comparator = Comparator.comparing(Category::getId);
        } else if (sortBy.equals("name")) {
            comparator = Comparator.comparing(Category::getName);
        } else if (sortBy.equals("creation")) {
            comparator = Comparator.comparing(category -> category.getCreation());
        } else {
            comparator = Comparator.comparing(Category::getId);
        }
        
        if (sortDir.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
        
        categories.sort(comparator);
        
        // Aplicar paginación
        int totalItems = categories.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalItems);
        
        List<Category> pagedCategories = fromIndex < totalItems ? 
                categories.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", pagedCategories);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar una categoría a una tarea del usuario autenticado")
    @PostMapping("/{taskId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToTask(@PathVariable Long taskId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (taskOpt.isPresent() && categoryOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().add(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de una tarea del usuario autenticado")
    @DeleteMapping("/{taskId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromTask(@PathVariable Long taskId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (taskOpt.isPresent() && categoryOpt.isPresent()) {
            Task task = taskOpt.get();
            task.getCategories().remove(categoryOpt.get());
            taskRepository.save(task);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las notas relacionadas con una tarea del usuario autenticado")
    @GetMapping("/{id}/notes")
    public ResponseEntity<Set<Note>> getTaskNotes(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(task -> ResponseEntity.ok(task.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todos los hábitos relacionados con una tarea del usuario autenticado")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getTaskHabits(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return taskRepository.findByIdAndUser(id, currentUser)
                .map(task -> ResponseEntity.ok(task.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // History Endpoints

    @Operation(summary = "Obtener el historial de versiones de una tarea del usuario autenticado")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@Parameter(description = "ID de la tarea") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que la tarea pertenece al usuario autenticado
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(id, currentUser);
        if (!taskOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<TaskHistory> history = taskHistoryRepository.findByTask_IdAndUserOrderByVersionIdDesc(id, currentUser);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener una versión específica del historial de una tarea del usuario autenticado")
    @GetMapping("/{id}/history/{versionId}")
    public ResponseEntity<TaskHistory> getTaskHistoryByVersion(
            @Parameter(description = "ID de la tarea") @PathVariable Long id,
            @Parameter(description = "ID de la versión del historial") @PathVariable Long versionId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que la tarea pertenece al usuario autenticado
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(id, currentUser);
        if (!taskOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<TaskHistory> historyVersion = taskHistoryRepository.findByTask_IdAndVersionIdAndUser(id, versionId, currentUser);
        if (!historyVersion.isEmpty()) {
            return ResponseEntity.ok(historyVersion.get(0)); // Assuming versionId is unique for each task
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private void saveTaskHistory(Task task) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setUser(getCurrentUser()); // Assign current user for data isolation
        history.setTitle(task.getTitle());
        history.setDescription(task.getDescription());
        history.setCreation(task.getCreation());
        history.setTimestamp(new Date()); // Current timestamp
        // You might want to implement a more robust versioning logic here, e.g., incrementing versionId
        // For simplicity, we are not setting versionId for now.

        taskHistoryRepository.save(history);
    }
}