package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "HabitTasks")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HabitTask {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private HabitTaskId id = new HabitTaskId();

    @ManyToOne
    @MapsId("habitId")
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "TaskId")
    private Task task;

    @Data
    @Embeddable
    @EqualsAndHashCode
    public static class HabitTaskId implements java.io.Serializable {
        private Long habitId;
        private Long taskId;
    }
}