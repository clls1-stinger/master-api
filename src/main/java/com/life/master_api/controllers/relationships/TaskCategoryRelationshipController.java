package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Task;
import com.life.master_api.entities.relationships.TaskCategoryRelationship;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.TaskRepository;
import com.life.master_api.repositories.relationships.TaskCategoryRelationshipRepository;
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
@RequestMapping("/task-categories")
public class TaskCategoryRelationshipController {

    private final TaskCategoryRelationshipRepository taskCategoryRelationshipRepository;
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskCategoryRelationshipController(TaskCategoryRelationshipRepository taskCategoryRelationshipRepository, TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskCategoryRelationshipRepository = taskCategoryRelationshipRepository;
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Obtener todas las relaciones TaskCategory")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones TaskCategory obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<TaskCategoryRelationship>> getAllTaskCategoryRelationships() {
        return ResponseEntity.ok(taskCategoryRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación TaskCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación TaskCategory creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<TaskCategoryRelationship> createTaskCategoryRelationship(@RequestParam Long taskId, @RequestParam Long categoryId) {
        Optional<Task> task = taskRepository.findById(taskId);
        Optional<Category> category = categoryRepository.findById(categoryId);

        if (task.isPresent() && category.isPresent()) {
            TaskCategoryRelationship relationship = new TaskCategoryRelationship(task.get(), category.get());
            TaskCategoryRelationship savedRelationship = taskCategoryRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones TaskCategory por ID de tarea")
    @ApiResponse(responseCode = "200", description = "Relaciones TaskCategory encontradas por Task ID")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskCategoryRelationship>> getTaskCategoryRelationshipsByTaskId(@PathVariable Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            List<TaskCategoryRelationship> relationships = taskCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getTask().getId().equals(taskId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones TaskCategory por ID de categoría")
    @ApiResponse(responseCode = "200", description = "Relaciones TaskCategory encontradas por Category ID")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TaskCategoryRelationship>> getTaskCategoryRelationshipsByCategoryId(@PathVariable Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            List<TaskCategoryRelationship> relationships = taskCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Eliminar una relación TaskCategory por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación TaskCategory eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación TaskCategory no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskCategoryRelationship(@PathVariable Long id) {
        return taskCategoryRelationshipRepository.findById(id)
                .map(relationship -> {
                    taskCategoryRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}