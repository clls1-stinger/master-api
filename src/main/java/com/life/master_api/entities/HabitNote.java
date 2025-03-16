package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "HabitNotes")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HabitNote {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private HabitNoteId id = new HabitNoteId();

    @ManyToOne
    @MapsId("habitId")
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @MapsId("noteId")
    @JoinColumn(name = "NoteId")
    private Note note;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class HabitNoteId implements java.io.Serializable {
        private Long habitId;
        private Long noteId;
    }
}