package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.HabitHistory;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.User;
import com.life.master_api.exceptions.ResourceNotFoundException;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitHistoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;

@RestController
@RequestMapping("/api/v1/habits")
@Tag(name = "Habits", description = "API para gestión de hábitos")
@SecurityRequirement(name = "bearerAuth")
public class HabitController {

    private final HabitRepository habitRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public HabitController(HabitRepository habitRepository,
                             HabitHistoryRepository habitHistoryRepository,
                             CategoryRepository categoryRepository,
                             NoteRepository noteRepository,
                             TaskRepository taskRepository,
                             UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Operation(summary = "Obtener todos los hábitos del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de hábitos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHabits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Habit> habitPage = habitRepository.findByUser(currentUser, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", habitPage.getContent());
        response.put("currentPage", habitPage.getNumber());
        response.put("totalItems", habitPage.getTotalElements());
        response.put("totalPages", habitPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear un nuevo hábito para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hábito creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        habit.setUser(currentUser);
        habit.setCreation(new Date());
        Habit savedHabit = habitRepository.save(habit);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un hábito por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito encontrado"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabitById(@Parameter(description = "ID del hábito a obtener") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar un hábito existente del usuario autenticado (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                            @Valid @RequestBody Habit habitDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(existingHabit -> {
                    // Save history before update
                    saveHabitHistory(existingHabit);

                    existingHabit.setName(habitDetails.getName());
                    Habit updatedHabit = habitRepository.save(existingHabit);
                    return ResponseEntity.ok(updatedHabit);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente un hábito existente del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito actualizado parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                                   @RequestBody Habit habitDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(existingHabit -> {
                    // Save history before update
                    saveHabitHistory(existingHabit);

                    if (habitDetails.getName() != null) {
                        existingHabit.setName(habitDetails.getName());
                    }
                    Habit updatedHabit = habitRepository.save(existingHabit);
                    return ResponseEntity.ok(updatedHabit);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un hábito por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hábito eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@Parameter(description = "ID del hábito a eliminar") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las categorías de un hábito del usuario autenticado")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Map<String, Object>> getHabitCategories(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(id, currentUser);
        if (!habitOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Habit habit = habitOpt.get();
        List<Category> categories = new ArrayList<>(habit.getCategories());
        
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

    @Operation(summary = "Agregar una categoría a un hábito del usuario autenticado")
    @PostMapping("/{habitId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (habitOpt.isPresent() && categoryOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().add(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de un hábito del usuario autenticado")
    @DeleteMapping("/{habitId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (habitOpt.isPresent() && categoryOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().remove(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las notas relacionadas con un hábito del usuario autenticado")
    @GetMapping("/{id}/notes")
    public ResponseEntity<Set<Note>> getHabitNotes(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(habit -> ResponseEntity.ok(habit.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una nota a un hábito del usuario autenticado")
    @PostMapping("/{habitId}/notes/{noteId}")
    public ResponseEntity<Void> addNoteToHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);

        if (habitOpt.isPresent() && noteOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getNotes().add(noteOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una nota de un hábito del usuario autenticado")
    @DeleteMapping("/{habitId}/notes/{noteId}")
    public ResponseEntity<Void> removeNoteFromHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);

        if (habitOpt.isPresent() && noteOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getNotes().remove(noteOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las tareas relacionadas con un hábito del usuario autenticado")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Set<Task>> getHabitTasks(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return habitRepository.findByIdAndUser(id, currentUser)
                .map(habit -> ResponseEntity.ok(habit.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una tarea a un hábito del usuario autenticado")
    @PostMapping("/{habitId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);

        if (habitOpt.isPresent() && taskOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getTasks().add(taskOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una tarea de un hábito del usuario autenticado")
    @DeleteMapping("/{habitId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);

        if (habitOpt.isPresent() && taskOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getTasks().remove(taskOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // History Endpoints

    @Operation(summary = "Obtener el historial de versiones de un hábito del usuario autenticado")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitHistory>> getHabitHistory(@Parameter(description = "ID del hábito") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que el hábito pertenece al usuario autenticado
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(id, currentUser);
        if (!habitOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<HabitHistory> history = habitHistoryRepository.findByHabit_IdOrderByVersionIdDesc(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener una versión específica del historial de un hábito del usuario autenticado")
    @GetMapping("/{id}/history/{versionId}")
    public ResponseEntity<HabitHistory> getHabitHistoryByVersion(
            @Parameter(description = "ID del hábito") @PathVariable Long id,
            @Parameter(description = "ID de la versión del historial") @PathVariable Long versionId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que el hábito pertenece al usuario autenticado
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(id, currentUser);
        if (!habitOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<HabitHistory> historyVersion = habitHistoryRepository.findByHabit_IdAndVersionId(id, versionId);
        if (!historyVersion.isEmpty()) {
            return ResponseEntity.ok(historyVersion.get(0)); // Assuming versionId is unique for each habit
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private void saveHabitHistory(Habit habit) {
        HabitHistory history = new HabitHistory();
        history.setHabit(habit);
        history.setName(habit.getName());
        history.setCreation(habit.getCreation());
        history.setTimestamp(new Date()); // Current timestamp
        // You might want to implement a more robust versioning logic here, e.g., incrementing versionId
        // For simplicity, we are not setting versionId for now.

        habitHistoryRepository.save(history);
    }
}