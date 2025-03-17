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
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public HabitController(HabitRepository habitRepository, CategoryRepository categoryRepository, NoteRepository noteRepository, TaskRepository taskRepository) {
        this.habitRepository = habitRepository;
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
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @Valid @RequestBody Habit habitDetails,
                                             @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                             @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                             @RequestParam(value = "taskIds", required = false) List<Long> taskIds) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
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
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @RequestBody Habit habitDetails,
                                                    @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                    @RequestParam(value = "noteIds", required = false) List<Long> noteIds,
                                                    @RequestParam(value = "taskIds", required = false) List<Long> taskIds) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
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
}