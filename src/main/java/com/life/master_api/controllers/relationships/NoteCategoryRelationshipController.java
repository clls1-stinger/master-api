package com.life.master_api.controllers.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import com.life.master_api.entities.relationships.NoteCategoryRelationship;
import com.life.master_api.repositories.CategoryRepository;
import com.life.master_api.repositories.NoteRepository;
import com.life.master_api.repositories.relationships.NoteCategoryRelationshipRepository;
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
@RequestMapping("/note-categories")
public class NoteCategoryRelationshipController {

    private final NoteCategoryRelationshipRepository noteCategoryRelationshipRepository;
    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;

    public NoteCategoryRelationshipController(NoteCategoryRelationshipRepository noteCategoryRelationshipRepository, NoteRepository noteRepository, CategoryRepository categoryRepository) {
        this.noteCategoryRelationshipRepository = noteCategoryRelationshipRepository;
        this.noteRepository = noteRepository;
        this.categoryRepository = categoryRepository;
    }

    @Operation(summary = "Obtener todas las relaciones NoteCategory")
    @ApiResponse(responseCode = "200", description = "Lista de relaciones NoteCategory obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<NoteCategoryRelationship>> getAllNoteCategoryRelationships() {
        return ResponseEntity.ok(noteCategoryRelationshipRepository.findAll());
    }

    @Operation(summary = "Crear una nueva relación NoteCategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Relación NoteCategory creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<NoteCategoryRelationship> createNoteCategoryRelationship(@RequestParam Long noteId, @RequestParam Long categoryId) {
        Optional<Note> note = noteRepository.findById(noteId);
        Optional<Category> category = categoryRepository.findById(categoryId);

        if (note.isPresent() && category.isPresent()) {
            NoteCategoryRelationship relationship = new NoteCategoryRelationship(note.get(), category.get());
            NoteCategoryRelationship savedRelationship = noteCategoryRelationshipRepository.save(relationship);
            return new ResponseEntity<>(savedRelationship, HttpStatus.CREATED);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener relaciones NoteCategory por ID de nota")
    @ApiResponse(responseCode = "200", description = "Relaciones NoteCategory encontradas por Note ID")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<NoteCategoryRelationship>> getNoteCategoryRelationshipsByNoteId(@PathVariable Long noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        if (note.isPresent()) {
            List<NoteCategoryRelationship> relationships = noteCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getNote().getId().equals(noteId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Obtener relaciones NoteCategory por ID de categoría")
    @ApiResponse(responseCode = "200", description = "Relaciones NoteCategory encontradas por Category ID")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<NoteCategoryRelationship>> getNoteCategoryRelationshipsByCategoryId(@PathVariable Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isPresent()) {
            List<NoteCategoryRelationship> relationships = noteCategoryRelationshipRepository.findAll().stream()
                    .filter(rel -> rel.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(relationships);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar una relación NoteCategory por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relación NoteCategory eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Relación NoteCategory no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteCategoryRelationship(@PathVariable Long id) {
        return noteCategoryRelationshipRepository.findById(id)
                .map(relationship -> {
                    noteCategoryRelationshipRepository.delete(relationship);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}