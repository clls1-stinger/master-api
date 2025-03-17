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

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;

    public NoteController(NoteRepository noteRepository, 
                         CategoryRepository categoryRepository, 
                         TaskRepository taskRepository, 
                         HabitRepository habitRepository) {
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener todas las notas")
    @ApiResponse(responseCode = "200", description = "Lista de notas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        return ResponseEntity.ok(noteRepository.findAll());
    }

    @Operation(summary = "Crear una nueva nota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        note.setCreation(new Date());
        Note savedNote = noteRepository.save(note);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una nota por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@Parameter(description = "ID de la nota a obtener") @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una nota existente (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, 
                                          @Valid @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    existingNote.setTitle(noteDetails.getTitle());
                    existingNote.setNote(noteDetails.getNote());
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
    @PatchMapping("/{id}")
    public ResponseEntity<Note> partialUpdateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id, 
                                                 @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
                .map(existingNote -> {
                    if (noteDetails.getTitle() != null) {
                        existingNote.setTitle(noteDetails.getTitle());
                    }
                    if (noteDetails.getNote() != null) {
                        existingNote.setNote(noteDetails.getNote());
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@Parameter(description = "ID de la nota a eliminar") @PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> {
                    noteRepository.delete(note);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

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

    @Operation(summary = "Agregar una tarea a una nota")
    @PostMapping("/{noteId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (noteOpt.isPresent() && taskOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getTasks().add(taskOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una tarea de una nota")
    @DeleteMapping("/{noteId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (noteOpt.isPresent() && taskOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getTasks().remove(taskOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todos los hábitos relacionados con una nota")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getNoteHabits(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(note -> ResponseEntity.ok(note.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar un hábito a una nota")
    @PostMapping("/{noteId}/habits/{habitId}")
    public ResponseEntity<Void> addHabitToNote(@PathVariable Long noteId, @PathVariable Long habitId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        
        if (noteOpt.isPresent() && habitOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getHabits().add(habitOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un hábito de una nota")
    @DeleteMapping("/{noteId}/habits/{habitId}")
    public ResponseEntity<Void> removeHabitFromNote(@PathVariable Long noteId, @PathVariable Long habitId) {
        Optional<Note> noteOpt = noteRepository.findById(noteId);
        Optional<Habit> habitOpt = habitRepository.findById(habitId);
        
        if (noteOpt.isPresent() && habitOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getHabits().remove(habitOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }
}