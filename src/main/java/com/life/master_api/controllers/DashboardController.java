package com.life.master_api.controllers;

import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.HabitRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity; // <--- ADD THIS IMPORT STATEMENT

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard-stats")
public class DashboardController {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;
    private final HabitRepository habitRepository;

    public DashboardController(
            CategoryRepository categoryRepository,
            TaskRepository taskRepository,
            NoteRepository noteRepository,
            HabitRepository habitRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
        this.noteRepository = noteRepository;
        this.habitRepository = habitRepository;
    }

    @Operation(summary = "Obtener estadísticas del dashboard")
    @ApiResponse(responseCode = "200", description = "Estadísticas del dashboard obtenidas exitosamente")
    // GET /dashboard-stats
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get counts for each entity
        long categoryCount = categoryRepository.count();
        long taskCount = taskRepository.count();
        long noteCount = noteRepository.count();
        long habitCount = habitRepository.count();

        // Add to stats map
        stats.put("categoryCount", categoryCount);
        stats.put("taskCount", taskCount);
        stats.put("noteCount", noteCount);
        stats.put("habitCount", habitCount);
        stats.put("totalEntities", categoryCount + taskCount + noteCount + habitCount);

        return ResponseEntity.ok(stats);
    }
}