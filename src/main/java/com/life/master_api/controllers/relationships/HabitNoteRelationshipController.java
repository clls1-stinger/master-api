package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.relationships.HabitNoteRelationship;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.relationships.HabitNoteRelationshipRepository;
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
@RequestMapping("/habit-notes")
public class HabitNoteRelationshipController {

    private final HabitNoteRelationshipRepository habitNoteRelationshipRepository;
    private final HabitRepository habitRepository;
    private final NoteRepository noteRepository;

    public HabitNoteRelationshipController(HabitNoteRelationshipRepository habitNoteRelationshipRepository, HabitRepository habitRepository, NoteRepository noteRepository) {
        this.habitNoteRelationshipRepository = habitNoteRelationshipRepository;
        this.habitRepository = habitRepository;
        this.noteRepository = noteRepository;
    }

    @Operation(summary = "Obtener todas las relaciones HabitNote")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones HabitNote obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<HabitNoteRelationship>> getAllHabitNoteRelationships() {
        return ResponseEntity.ok(habitNoteRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación HabitNote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación HabitNote creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<HabitNoteRelationship> createHabitNoteRelationship(@RequestParam Long habitId, @RequestParam Long noteId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        Optional<Note> note = noteRepository.findById(noteId);

        if (habit.isPresent() && note.isPresent()) {
            HabitNoteRelationship relationship = new HabitNoteRelationship(habit.get(), note.get());
            HabitNoteRelationship savedRelationship = habitNoteRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitNote por ID de hábito")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitNote encontradas por Habit ID")
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitNoteRelationship>> getHabitNoteRelationshipsByHabitId(@PathVariable Long habitId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            List<HabitNoteRelationship> relationships = habitNoteRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getHabit().getId().equals(habitId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitNote por ID de nota")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitNote encontradas por Note ID")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<HabitNoteRelationship>> getHabitNoteRelationshipsByNoteId(@PathVariable Long noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            List<HabitNoteRelationship> relationships = habitNoteRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getNote().getId().equals(noteId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una relación HabitNote por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación HabitNote eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación HabitNote no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitNoteRelationship(@PathVariable Long id) {
        return habitNoteRelationshipRepository.findById(id)
                .map(relationship -> {
                    habitNoteRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}