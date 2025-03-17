package com.life.master_api.entities.relationships;

import com.life.master_api.entities.Habit;
import com.life.master_api.entities.Task;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "HabitTasks")
@Data
public class HabitTaskRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "HabitId")
    private Habit habit;

    @ManyToOne
    @JoinColumn(name = "TaskId")
    private Task task;

    public HabitTaskRelationship() {
    }

    public HabitTaskRelationship(Habit habit, Task task) {
        this.habit = habit;
        this.task = task;
    }
}