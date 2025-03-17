package com.life.master_api.controllers;

import com.life.master_api.entities.Task;
import com.life.master_api.entities.Habit;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/habits/{habitId}/tasks")
public class HabitTaskController {

    private final HabitRepository habitRepository;
    private final TaskRepository taskRepository;

    public HabitTaskController(HabitRepository habitRepository, TaskRepository taskRepository) {
        this.habitRepository = habitRepository;
        this.taskRepository = taskRepository;
    }

    // GET /habits/{habitId}/tasks
    @GetMapping
    public ResponseEntity<Set<Task>> getTasksForHabit(@PathVariable Long habitId) {
        return habitRepository.findById(habitId)
                .map(habit -> ResponseEntity.ok(habit.getTasks()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /habits/{habitId}/tasks/{taskId}
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addTaskToHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> taskRepository.findById(taskId)
                        .map(task -> {
                            habit.getTasks().add(task);
                            habitRepository.save(habit);
                            return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE /habits/{habitId}/tasks/{taskId}
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTaskFromHabit(@PathVariable Long habitId, @PathVariable Long taskId) {
        return habitRepository.findById(habitId)
                .flatMap(habit -> taskRepository.findById(taskId)
                        .map(task -> {
                            habit.getTasks().remove(task);
                            habitRepository.save(habit);
                            return ResponseEntity.noContent().<Void>build();
                        }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}