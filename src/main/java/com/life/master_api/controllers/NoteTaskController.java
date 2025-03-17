package com.life.master_api.controllers;

import com.life.master_api.entities.Task;
import com.life.master_api.entities.Note;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/notes/{noteId}/tasks")
public class NoteTaskController {

    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public NoteTaskController(NoteRepository noteRepository, TaskRepository taskRepository) {
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    // GET /notes/{noteId}/tasks
    @GetMapping
    public ResponseEntity<Set<Task>> getTasksForNote(@PathVariable Long noteId) {
        return noteRepository.findById(noteId)
                .map(note -> ResponseEntity.ok(note.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /notes/{noteId}/tasks/{taskId}
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addTaskToNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        return noteRepository.findById(noteId)
                .flatMap(note -> taskRepository.findById(taskId)
                        .map(task -> {
                            note.getTasks().add(task);
                            noteRepository.save(note);
                            return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /notes/{noteId}/tasks/{taskId}
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTaskFromNote(@PathVariable Long noteId, @PathVariable Long taskId) {
        return noteRepository.findById(noteId)
                .flatMap(note -> taskRepository.findById(taskId)
                        .map(task -> {
                            note.getTasks().remove(task);
                            noteRepository.save(note);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}