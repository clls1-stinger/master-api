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
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;

    public NoteController(NoteRepository noteRepository, CategoryRepository categoryRepository, TaskRepository taskRepository, HabitRepository habitRepository) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener todas las notas")
    @ApiResponse(responseCode = "200", description = "Lista de notas obtenida exitosamente")
    // GET /notes
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        return ResponseEntity.ok(noteRepository.findAll());
    }

    @Operation(summary = "Crear una nueva nota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // POST /notes
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note,
                                           @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                           @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                           @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        note.setCreation(new Date());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
            note.setCategories(categories);
        }
        if (taskIds != null && !taskIds.isEmpty()) {
            Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
            note.setTasks(tasks);
        }
        if (habitIds != null && !habitIds.isEmpty()) {
            Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
            note.setHabits(habits);
        }

        Note savedNote = noteRepository.save(note);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una nota por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    // GET /notes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@Parameter(description = "ID de la nota a obtener") @PathVariable Long id) {
        Optional<Note> note = noteRepository.findById(id);
        return note.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una nota existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    // PUT /notes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, @Valid @RequestBody Note noteDetails,
                                           @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                           @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                           @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setTitle(noteDetails.getTitle());
                    existingNote.setNote(noteDetails.getNote());

                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
                        existingNote.setCategories(categories);
                    } else {
                        existingNote.getCategories().clear();
                    }
                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingNote.setTasks(tasks);
                    } else {
                        existingNote.getTasks().clear();
                    }
                    if (habitIds != null && !habitIds.isEmpty()) {
                        Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
                        existingNote.setHabits(habits);
                    } else {
                        existingNote.getHabits().clear();
                    }

                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente una nota existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    // PATCH /notes/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Note> partialUpdateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, @RequestBody Note noteDetails,
                                                  @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                  @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                  @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    if (noteDetails.getTitle() != null) {
                        existingNote.setTitle(noteDetails.getTitle());
                    }
                    if (noteDetails.getNote() != null) {
                        existingNote.setNote(noteDetails.getNote());
                    }

                    if (categoryIds != null && !categoryIds.isEmpty()) {
                        Set<Category> categories = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toSet());
                        existingNote.setCategories(categories);
                    }
                    if (taskIds != null && !taskIds.isEmpty()) {
                        Set<Task> tasks = taskRepository.findAllById(taskIds).stream().collect(Collectors.toSet());
                        existingNote.setTasks(tasks);
                    }
                    if (habitIds != null && !habitIds.isEmpty()) {
                        Set<Habit> habits = habitRepository.findAllById(habitIds).stream().collect(Collectors.toSet());
                        existingNote.setHabits(habits);
                    }

                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una nota por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    // DELETE /notes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@Parameter(description = "ID de la nota a eliminar") @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    noteRepository.delete(note);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}