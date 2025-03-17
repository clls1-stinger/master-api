package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Category;
import com.life.master_api.entities.Note;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "NoteCategories")
@Data
public class NoteCategoryRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "NoteId")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    public NoteCategoryRelationship() {
    }

    public NoteCategoryRelationship(Note note, Category category) {
        this.note = note;
        this.category = category;
    }
}