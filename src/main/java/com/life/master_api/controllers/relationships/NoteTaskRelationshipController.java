package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.relationships.NoteTaskRelationship;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.relationships.NoteTaskRelationshipRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/note-tasks")
public class NoteTaskRelationshipController {

    private final NoteTaskRelationshipRepository noteTaskRelationshipRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public NoteTaskRelationshipController(NoteTaskRelationshipRepository noteTaskRelationshipRepository, NoteRepository noteRepository, TaskRepository taskRepository) {
        this.noteTaskRelationshipRepository = noteTaskRelationshipRepository;
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Obtener todas las relaciones NoteTask")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones NoteTask obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<NoteTaskRelationship>> getAllNoteTaskRelationships() {
        return ResponseEntity.ok(noteTaskRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación NoteTask")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación NoteTask creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<NoteTaskRelationship> createNoteTaskRelationship(@RequestParam Long noteId, @RequestParam Long taskId) {
        Optional<Note> note = noteRepository.findById(noteId);
        Optional<Task> task = taskRepository.findById(taskId);

        if (note.isPresent() && task.isPresent()) {
            NoteTaskRelationship relationship = new NoteTaskRelationship(note.get(), task.get());
            NoteTaskRelationship savedRelationship = noteTaskRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones NoteTask por ID de nota")
    @ApiResponse(responseCode = "200", description = "Relaciones NoteTask encontradas por Note ID")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<NoteTaskRelationship>> getNoteTaskRelationshipsByNoteId(@PathVariable Long noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            List<NoteTaskRelationship> relationships = noteTaskRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getNote().getId().equals(noteId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones NoteTask por ID de tarea")
    @ApiResponse(responseCode = "200", description = "Relaciones NoteTask encontradas por Task ID")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<NoteTaskRelationship>> getNoteTaskRelationshipsByTaskId(@PathVariable Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            List<NoteTaskRelationship> relationships = noteTaskRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getTask().getId().equals(taskId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una relación NoteTask por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación NoteTask eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación NoteTask no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteTaskRelationship(@PathVariable Long id) {
        return noteTaskRelationshipRepository.findById(id)
                .map(relationship -> {
                    noteTaskRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}