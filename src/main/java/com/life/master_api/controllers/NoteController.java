package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.NoteHistory;
import com.life.master_api.entities.Task;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteHistoryRepository;
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
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final NoteHistoryRepository noteHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;

<<<<<<< Updated upstream
    public NoteController(NoteRepository noteRepository, CategoryRepository categoryRepository, TaskRepository taskRepository, HabitRepository habitRepository) {
=======
    public NoteController(NoteRepository noteRepository,
                          NoteHistoryRepository noteHistoryRepository,
                          CategoryRepository categoryRepository,
                          TaskRepository taskRepository,
                          HabitRepository habitRepository) {
>>>>>>> Stashed changes
        this.noteRepository = noteRepository;
        this.noteHistoryRepository = noteHistoryRepository;
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
<<<<<<< Updated upstream
    public ResponseEntity<Note> updateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, @Valid @RequestBody Note noteDetails,
                                           @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                           @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                           @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
=======
    public ResponseEntity<Note> updateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id,
                                           @Valid @RequestBody Note noteDetails) {
>>>>>>> Stashed changes
        return noteRepository.findById(id)
                .map(existingNote -> {
                    // Guarda el historial antes de actualizar
                    saveNoteHistory(existingNote);

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
<<<<<<< Updated upstream
    public ResponseEntity<Note> partialUpdateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, @RequestBody Note noteDetails,
                                                  @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                                  @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                                  @RequestParam(value = "habitIds", required = false) List<Long> habitIds) {
=======
    public ResponseEntity<Note> patchNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id,
                                          @RequestBody Note noteDetails) {
>>>>>>> Stashed changes
        return noteRepository.findById(id)
                .map(existingNote -> {
                    // Guarda el historial antes de actualizar
                    saveNoteHistory(existingNote);

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
<<<<<<< Updated upstream
=======

    // Endpoints para historial de notas

    @Operation(summary = "Obtener el historial de versiones de una nota")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<NoteHistory>> getNoteHistory(@Parameter(description = "ID de la nota") @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    List<NoteHistory> historyList = new ArrayList<>(note.getHistory());
                    historyList.sort(Comparator.comparing(NoteHistory::getModificationTimestamp).reversed()); // Ordenar por fecha descendente
                    return ResponseEntity.ok(historyList);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener una versión específica del historial de nota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Versión del historial de nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Versión del historial de nota no encontrada")
    })
    @GetMapping("/{id}/history/{historyId}")
    public ResponseEntity<NoteHistory> getNoteHistoryVersion(
            @Parameter(description = "ID de la nota") @PathVariable Long id,
            @Parameter(description = "ID del historial de la nota") @PathVariable Long historyId) {
        return noteHistoryRepository.findById(historyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Métodos auxiliares

    private void saveNoteHistory(Note note) {
        NoteHistory history = new NoteHistory();
        history.setNoteId(note.getId());
        history.setTitle(note.getTitle());
        history.setNote(note.getNote());
        history.setCreation(note.getCreation());
        history.setModificationTimestamp(new Date()); // Fecha actual como timestamp de modificación
        noteHistoryRepository.save(history);
    }


    // Endpoints para gestionar relaciones (sin cambios en esta parte por ahora)

    @Operation(summary = "Obtener todas las categorías de una nota")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Set<Category>> getNoteCategories(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(note.getCategories()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una categoría a una nota")
    @PostMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (noteOpt.isPresent() && categoryOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().add(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de una nota")
    @DeleteMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (noteOpt.isPresent() && categoryOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().remove(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las tareas relacionadas con una nota")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Set<Task>> getNoteTasks(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(note.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener todos los hábitos relacionados con una nota")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getNoteHabits(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(note.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
>>>>>>> Stashed changes
}