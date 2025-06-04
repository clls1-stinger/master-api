package com.life.master_api.controllers;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.NoteHistory;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.User;
import com.life.master_api.exceptions.ResourceNotFoundException;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteHistoryRepository;
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
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes", description = "API para gestión de notas")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {

    private final NoteRepository noteRepository;
    private final NoteHistoryRepository noteHistoryRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public NoteController(NoteRepository noteRepository,
                            NoteHistoryRepository noteHistoryRepository,
                            CategoryRepository categoryRepository,
                            TaskRepository taskRepository,
                            HabitRepository habitRepository,
                            UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.noteHistoryRepository = noteHistoryRepository;
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene el usuario autenticado actualmente
     * @return Usuario autenticado
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Operation(summary = "Obtener todas las notas del usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de notas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Note> notePage = noteRepository.findByUser(currentUser, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", notePage.getContent());
        response.put("currentPage", notePage.getNumber());
        response.put("totalItems", notePage.getTotalElements());
        response.put("totalPages", notePage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva nota para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        note.setCreation(new Date());
        note.setUser(currentUser);
        Note savedNote = noteRepository.save(note);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener una nota por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada o no pertenece al usuario")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@Parameter(description = "ID de la nota a obtener") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar una nota existente del usuario autenticado (reemplazo completo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada o no pertenece al usuario"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id,
                                          @Valid @RequestBody Note noteDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(existingNote -> {
                    // Save history before update
                    saveNoteHistory(existingNote);

                    existingNote.setTitle(noteDetails.getTitle());
                    existingNote.setNote(noteDetails.getNote());
                    // Mantener el usuario actual
                    existingNote.setUser(currentUser);
                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar parcialmente una nota existente del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada parcialmente exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada o no pertenece al usuario")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Note> partialUpdateNote(@Parameter(description = "ID de la nota a actualizar") @PathVariable Long id,
                                                 @RequestBody Note noteDetails) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(existingNote -> {
                    // Save history before update
                    saveNoteHistory(existingNote);

                    if (noteDetails.getTitle() != null) {
                        existingNote.setTitle(noteDetails.getTitle());
                    }
                    if (noteDetails.getNote() != null) {
                        existingNote.setNote(noteDetails.getNote());
                    }
                    // Mantener el usuario actual
                    existingNote.setUser(currentUser);
                    Note updatedNote = noteRepository.save(existingNote);
                    return ResponseEntity.ok(updatedNote);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una nota por ID del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada o no pertenece al usuario")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@Parameter(description = "ID de la nota a eliminar") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(note -> {
                    noteRepository.delete(note);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoints para gestionar relaciones

    @Operation(summary = "Obtener todas las categorías de una nota del usuario autenticado")
    @GetMapping("/{id}/categories")
    public ResponseEntity<Map<String, Object>> getNoteCategories(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(id, currentUser);
        if (!noteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Note note = noteOpt.get();
        List<Category> categories = new ArrayList<>(note.getCategories());
        
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

    @Operation(summary = "Agregar una categoría a una nota del usuario autenticado")
    @PostMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (noteOpt.isPresent() && categoryOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().add(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría de una nota del usuario autenticado")
    @DeleteMapping("/{noteId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromNote(@PathVariable Long noteId, @PathVariable Long categoryId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Category> categoryOpt = categoryRepository.findByIdAndUser(categoryId, currentUser);

        if (noteOpt.isPresent() && categoryOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getCategories().remove(categoryOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todas las tareas relacionadas con una nota del usuario autenticado")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<Set<Task>> getNoteTasks(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(note -> ResponseEntity.ok(note.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar una tarea a una nota del usuario autenticado")
    @PostMapping("/{noteId}/tasks/{taskId}")
    public ResponseEntity<Void> addTaskToNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);

        if (noteOpt.isPresent() && taskOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getTasks().add(taskOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una tarea de una nota del usuario autenticado")
    @DeleteMapping("/{noteId}/tasks/{taskId}")
    public ResponseEntity<Void> removeTaskFromNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Task> taskOpt = taskRepository.findByIdAndUser(taskId, currentUser);

        if (noteOpt.isPresent() && taskOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getTasks().remove(taskOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener todos los hábitos relacionados con una nota del usuario autenticado")
    @GetMapping("/{id}/habits")
    public ResponseEntity<Set<Habit>> getNoteHabits(@PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        return noteRepository.findByIdAndUser(id, currentUser)
                .map(note -> ResponseEntity.ok(note.getHabits()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Agregar un hábito a una nota del usuario autenticado")
    @PostMapping("/{noteId}/habits/{habitId}")
    public ResponseEntity<Void> addHabitToNote(@PathVariable Long noteId, @PathVariable Long habitId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);

        if (noteOpt.isPresent() && habitOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getHabits().add(habitOpt.get());
            noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un hábito de una nota del usuario autenticado")
    @DeleteMapping("/{noteId}/habits/{habitId}")
    public ResponseEntity<Void> removeHabitFromNote(@PathVariable Long noteId, @PathVariable Long habitId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(noteId, currentUser);
        Optional<Habit> habitOpt = habitRepository.findByIdAndUser(habitId, currentUser);

        if (noteOpt.isPresent() && habitOpt.isPresent()) {
            Note note = noteOpt.get();
            note.getHabits().remove(habitOpt.get());
            noteRepository.save(note);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // History Endpoints

    @Operation(summary = "Obtener el historial de versiones de una nota del usuario autenticado")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<NoteHistory>> getNoteHistory(@Parameter(description = "ID de la nota") @PathVariable Long id) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que la nota pertenece al usuario autenticado
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(id, currentUser);
        if (!noteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<NoteHistory> history = noteHistoryRepository.findByNote_IdOrderByVersionIdDesc(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener una versión específica del historial de una nota del usuario autenticado")
    @GetMapping("/{id}/history/{versionId}")
    public ResponseEntity<NoteHistory> getNoteHistoryByVersion(
            @Parameter(description = "ID de la nota") @PathVariable Long id,
            @Parameter(description = "ID de la versión del historial") @PathVariable Long versionId) {
        // Obtener el usuario autenticado
        User currentUser = getCurrentUser();
        
        // Verificar que la nota pertenece al usuario autenticado
        Optional<Note> noteOpt = noteRepository.findByIdAndUser(id, currentUser);
        if (!noteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<NoteHistory> historyVersion = noteHistoryRepository.findByNote_IdAndVersionId(id, versionId);
        if (!historyVersion.isEmpty()) {
            return ResponseEntity.ok(historyVersion.get(0)); // Assuming versionId is unique for each note
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private void saveNoteHistory(Note note) {
        NoteHistory history = new NoteHistory();
        history.setNote(note);
        history.setTitle(note.getTitle());
        history.setNoteContent(note.getNote()); // Corrected line: using setNoteContent
        history.setCreation(note.getCreation());
        history.setTimestamp(new Date()); // Current timestamp
        // You might want to implement a more robust versioning logic here, e.g., incrementing versionId
        // For simplicity, we are not setting versionId for now.

        noteHistoryRepository.save(history);
    }
}