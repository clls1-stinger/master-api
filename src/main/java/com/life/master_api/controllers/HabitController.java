package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.HabitHistory;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitHistoryRepository;
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

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final HabitHistoryRepository habitHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public HabitController(HabitRepository habitRepository,
                             HabitHistoryRepository habitHistoryRepository,
                             CategoryRepository categoryRepository,
                             NoteRepository noteRepository,
                             TaskRepository taskRepository) {
        this.habitRepository = habitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Obtener todos los hábitos")
    @ApiResponse(responseCode = "200", description = "Lista de hábitos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits() {
        return ResponseEntity.ok(habitRepository.findAll());
    }

    @Operation(summary = "Crear un nuevo hábito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hábito creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit) {
        habit.setCreation(new Date());
        Habit savedHabit = habitRepository.save(habit);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un hábito por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito encontrado"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabitById(@Parameter(description = "ID del hábito a obtener") @PathVariable Long id) {
        return habitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar un hábito existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                            @Valid @RequestBody Habit habitDetails) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    // Save history before update
                    saveHabitHistory(existingHabit);

                    existingHabit.setName(habitDetails.getName());
                    Habit updatedHabit = habitRepository.save(existingHabit);
                    return ResponseEntity.ok(updatedHabit);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente un hábito existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito actualizado parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                                   @RequestBody Habit habitDetails) {
        return habitRepository.findById(id)
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

    @Operation(summary = "Eliminar un hábito por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hábito eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@Parameter(description = "ID del hábito a eliminar") @PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las categorías de un hábito")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Set<Category>> getHabitCategories(@PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> ResponseEntity.ok(habit.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una categoría a un hábito")
    @PostMapping("/{habitId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (habitOpt.isPresent() && categoryOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().add(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de un hábito")
    @DeleteMapping("/{habitId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromHabit(@PathVariable Long habitId, @PathVariable Long categoryId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (habitOpt.isPresent() && categoryOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getCategories().remove(categoryOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las notas relacionadas con un hábito")
    @GetMapping("/{id}/notes")
    public ResponseEntity<Set<Note>> getHabitNotes(@PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> ResponseEntity.ok(habit.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una nota a un hábito")
    @PostMapping("/{habitId}/notes/{noteId}")
    public ResponseEntity<Void> addNoteToHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Note> noteOpt = noteRepository.findById(noteId);

        if (habitOpt.isPresent() && noteOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getNotes().add(noteOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una nota de un hábito")
    @DeleteMapping("/{habitId}/notes/{noteId}")
    public ResponseEntity<Void> removeNoteFromHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Note> noteOpt = noteRepository.findById(noteId);

        if (habitOpt.isPresent() && habitOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getNotes().remove(noteOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las tareas relacionadas con un hábito")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Set<Task>> getHabitTasks(@PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> ResponseEntity.ok(habit.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una tarea a un hábito")
    @PostMapping("/{habitId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (habitOpt.isPresent() && taskOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getTasks().add(taskOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una tarea de un hábito")
    @DeleteMapping("/{habitId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (habitOpt.isPresent() && taskOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getTasks().remove(taskOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // History Endpoints

    @Operation(summary = "Obtener el historial de versiones de un hábito")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitHistory>> getHabitHistory(@Parameter(description = "ID del hábito") @PathVariable Long id) {
        List<HabitHistory> history = habitHistoryRepository.findByHabit_IdOrderByVersionIdDesc(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener una versión específica del historial de un hábito")
    @GetMapping("/{id}/history/{versionId}")
    public ResponseEntity<HabitHistory> getHabitHistoryByVersion(
            @Parameter(description = "ID del hábito") @PathVariable Long id,
            @Parameter(description = "ID de la versión del historial") @PathVariable Long versionId) {
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