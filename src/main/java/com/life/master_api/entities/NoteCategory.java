package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "NoteCategories")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NoteCategory {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private NoteCategoryId id = new NoteCategoryId();

    @ManyToOne
    @MapsId("noteId")
    @JoinColumn(name = "NoteId")
    private Note note;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class NoteCategoryId implements java.io.Serializable {
        private Long noteId;
        private Long categoryId;
    }
}