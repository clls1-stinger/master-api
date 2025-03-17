package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Note;
import com.life.master_api.entities.Task;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "NoteTasks")
@Data
public class NoteTaskRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "NoteId")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "TaskId")
    private Task task;

    public NoteTaskRelationship() {
    }

    public NoteTaskRelationship(Note note, Task task) {
        this.note = note;
        this.task = task;
    }
}