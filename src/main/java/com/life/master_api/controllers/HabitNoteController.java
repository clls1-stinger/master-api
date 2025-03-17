package com.life.master_api.controllers;

import com.life.master_api.entities.Note;
import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/habits/{habitId}/notes")
public class HabitNoteController {

    private final HabitRepository habitRepository;
    private final NoteRepository noteRepository;

    public HabitNoteController(HabitRepository habitRepository, NoteRepository noteRepository) {
        this.habitRepository = habitRepository;
        this.noteRepository = noteRepository;
    }

    // GET /habits/{habitId}/notes
    @GetMapping
    public ResponseEntity<Set<Note>> getNotesForHabit(@PathVariable Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> ResponseEntity.ok(habit.getNotes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /habits/{habitId}/notes/{noteId}
    @PostMapping("/{noteId}")
    public ResponseEntity<Void> addNoteToHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> noteRepository.findById(noteId)
                        .map(note -> {
                            habit.getNotes().add(note);
                            habitRepository.save(habit);
                            return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /habits/{habitId}/notes/{noteId}
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> removeNoteFromHabit(@PathVariable Long habitId, @PathVariable Long noteId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> noteRepository.findById(noteId)
                        .map(note -> {
                            habit.getNotes().remove(note);
                            habitRepository.save(habit);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}