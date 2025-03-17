package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Note;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "HabitNotes")
@Data
public class HabitNoteRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @JoinColumn(name = "NoteId")
    private Note note;

    public HabitNoteRelationship() {
    }

    public HabitNoteRelationship(Habit habit, Note note) {
        this.habit = habit;
        this.note = note;
    }
}