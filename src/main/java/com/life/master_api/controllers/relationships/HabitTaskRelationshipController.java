package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.relationships.HabitTaskRelationship;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.relationships.HabitTaskRelationshipRepository;
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
@RequestMapping("/habit-tasks")
public class HabitTaskRelationshipController {

    private final HabitTaskRelationshipRepository habitTaskRelationshipRepository;
    private final HabitRepository habitRepository;
    private final TaskRepository taskRepository;

    public HabitTaskRelationshipController(HabitTaskRelationshipRepository habitTaskRelationshipRepository, HabitRepository habitRepository, TaskRepository taskRepository) {
        this.habitTaskRelationshipRepository = habitTaskRelationshipRepository;
        this.habitRepository = habitRepository;
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Obtener todas las relaciones HabitTask")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones HabitTask obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<HabitTaskRelationship>> getAllHabitTaskRelationships() {
        return ResponseEntity.ok(habitTaskRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación HabitTask")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación HabitTask creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<HabitTaskRelationship> createHabitTaskRelationship(@RequestParam Long habitId, @RequestParam Long taskId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        Optional<Task> task = taskRepository.findById(taskId);

        if (habit.isPresent() && task.isPresent()) {
            HabitTaskRelationship relationship = new HabitTaskRelationship(habit.get(), task.get());
            HabitTaskRelationship savedRelationship = habitTaskRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitTask por ID de hábito")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitTask encontradas por Habit ID")
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<HabitTaskRelationship>> getHabitTaskRelationshipsByHabitId(@PathVariable Long habitId) {
        Optional<Habit> habit = habitRepository.findById(habitId);
        if (habit.isPresent()) {
            List<HabitTaskRelationship> relationships = habitTaskRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getHabit().getId().equals(habitId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones HabitTask por ID de tarea")
    @ApiResponse(responseCode = "200", description = "Relaciones HabitTask encontradas por Task ID")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<HabitTaskRelationship>> getHabitTaskRelationshipsByTaskId(@PathVariable Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            List<HabitTaskRelationship> relationships = habitTaskRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getTask().getId().equals(taskId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una relación HabitTask por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación HabitTask eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación HabitTask no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabitTaskRelationship(@PathVariable Long id) {
        return habitTaskRelationshipRepository.findById(id)
                .map(relationship -> {
                    habitTaskRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}