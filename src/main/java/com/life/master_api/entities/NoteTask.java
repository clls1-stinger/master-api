package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "NoteTasks")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NoteTask {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private NoteTaskId id = new NoteTaskId();

    @ManyToOne
    @MapsId("noteId")
    @JoinColumn(name = "NoteId")
    private Note note;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "TaskId")
    private Task task;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class NoteTaskId implements java.io.Serializable {
        private Long noteId;
        private Long taskId;
    }
}