package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.HabitHistory;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final HabitHistoryRepository habitHistoryRepository; // Inyecta el nuevo repositorio de historial
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

<<<<<<< Updated upstream
    public HabitController(HabitRepository habitRepository, CategoryRepository categoryRepository, NoteRepository noteRepository, TaskRepository taskRepository) {
=======
    public HabitController(HabitRepository habitRepository,
                           HabitHistoryRepository habitHistoryRepository,
                           CategoryRepository categoryRepository,
                           NoteRepository noteRepository,
                           TaskRepository taskRepository) {
>>>>>>> Stashed changes
        this.habitRepository = habitRepository;
        this.habitHistoryRepository = habitHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Obtener todos los hábitos")
    @ApiResponse(responseCode = "200", description = "Lista de hábitos obtenida exitosamente")
    // GET /habits
    @GetMapping
    public ResponseEntity<List<Habit>> getAllHabits() {
        return ResponseEntity.ok(habitRepository.findAll());
    }

    @Operation(summary = "Crear un nuevo hábito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hábito creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // POST /habits
    @PostMapping
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit,
                                             @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                             @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                             @RequestParam(value = "taskIds", required = false) List<Long> taskIds) {
        habit.setCreation(new Date());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
            habit.setCategories(categories);
        }
        if (noteIds != null && !noteIds.isEmpty()) {
            Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
            habit.setNotes(notes);
        }
        if (taskIds != null && !taskIds.isEmpty()) {
            Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
            habit.setTasks(tasks);
        }

        Habit savedHabit = habitRepository.save(habit);
        return new ResponseEntity<>(savedHabit, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener un hábito por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito encontrado"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado")
    })
    // GET /habits/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabitById(@Parameter(description = "ID del hábito a obtener") @PathVariable Long id) {
        Optional<Habit> habit = habitRepository.findById(id);
        return habit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar un hábito existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hábito actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Hábito no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // PUT /habits/{id}
    @PutMapping("/{id}")
<<<<<<< Updated upstream
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @Valid @RequestBody Habit habitDetails,
                                             @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                             @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                             @RequestParam(value = "taskIds", required = false) List<Long> taskIds) {
=======
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                             @Valid @RequestBody Habit habitDetails) {
>>>>>>> Stashed changes
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    // Guarda el historial antes de actualizar
                    saveHabitHistory(existingHabit);

                    existingHabit.setName(habitDetails.getName());

                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
                        existingHabit.setCategories(categories);
                    } else {
                        existingHabit.getCategories().clear();
                    }
                    if (noteIds != null && !noteIds.isEmpty()) {
                        Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
                        existingHabit.setNotes(notes);
                    } else {
                        existingHabit.getNotes().clear();
                    }
                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingHabit.setTasks(tasks);
                    } else {
                        existingHabit.getTasks().clear();
                    }

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
    // PATCH /habits/{id}
    @PatchMapping("/{id}")
<<<<<<< Updated upstream
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @RequestBody Habit habitDetails,
                                                    @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                    @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                    @RequestParam(value = "taskIds", required = false) List<Long> taskIds) {
=======
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id,
                                                    @RequestBody Habit habitDetails) {
>>>>>>> Stashed changes
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    // Guarda el historial antes de actualizar
                    saveHabitHistory(existingHabit);

                    if (habitDetails.getName() != null) {
                        existingHabit.setName(habitDetails.getName());
                    }

                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
                        existingHabit.setCategories(categories);
                    }
                    if (noteIds != null && !noteIds.isEmpty()) {
                        Set<Note> notes = noteRepository.findAllById(noteIds).stream().collect(Collectors.toSet());
                        existingHabit.setNotes(notes);
                    }
                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingHabit.setTasks(tasks);
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
    // DELETE /habits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@Parameter(description = "ID del hábito a eliminar") @PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> {
                    habitRepository.delete(habit);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
<<<<<<< Updated upstream
=======

    // Endpoints para historial de hábitos

    @Operation(summary = "Obtener el historial de versiones de un hábito")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitHistory>> getHabitHistory(@Parameter(description = "ID del hábito") @PathVariable Long id) {
        return habitRepository.findById(id)
                .map(habit -> {
                    List<HabitHistory> historyList = new ArrayList<>(habit.getHistory());
                    historyList.sort(Comparator.comparing(HabitHistory::getModificationTimestamp).reversed()); // Ordenar por fecha descendente
                    return ResponseEntity.ok(historyList);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener una versión específica del historial de hábito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Versión del historial de hábito encontrada"),
            @ApiResponse(responseCode = "404", description = "Versión del historial de hábito no encontrada")
    })
    @GetMapping("/{id}/history/{historyId}")
    public ResponseEntity<HabitHistory> getHabitHistoryVersion(
            @Parameter(description = "ID del hábito") @PathVariable Long id,
            @Parameter(description = "ID del historial del hábito") @PathVariable Long historyId) {
        return habitHistoryRepository.findById(historyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Métodos auxiliares

    private void saveHabitHistory(Habit habit) {
        HabitHistory history = new HabitHistory();
        history.setHabitId(habit.getId());
        history.setName(habit.getName());
        history.setCreation(habit.getCreation());
        history.setModificationTimestamp(new Date()); // Fecha actual como timestamp de modificación
        habitHistoryRepository.save(history);
    }


    // Endpoints para gestionar relaciones (sin cambios en esta parte por ahora)

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

        if (habitOpt.isPresent() && habitOpt.isPresent()) {
            Habit habit = habitOpt.get();
            habit.getTasks().remove(taskOpt.get());
            habitRepository.save(habit);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
>>>>>>> Stashed changes
}