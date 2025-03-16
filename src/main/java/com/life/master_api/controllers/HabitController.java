package com.life.master_api.controllers;

import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.HabitRepository;
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

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;

    public HabitController(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
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
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit) {
        habit.setCreation(new Date());
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
    public ResponseEntity<Habit> updateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @Valid @RequestBody Habit habitDetails) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    existingHabit.setName(habitDetails.getName());
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
    public ResponseEntity<Habit> partialUpdateHabit(@Parameter(description = "ID del hábito a actualizar") @PathVariable Long id, @RequestBody Habit habitDetails) {
        return habitRepository.findById(id)
                .map(existingHabit -> {
                    if (habitDetails.getName() != null) {
                        existingHabit.setName(habitDetails.getName());
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