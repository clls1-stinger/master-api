package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Task")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany
    @JoinTable(
            name = "TaskCategories",
            joinColumns = @JoinColumn(name = "TaskId"),
            inverseJoinColumns = @JoinColumn(name = "CategoryId")
    )
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "HabitTasks", // Reutilizamos HabitTasks
            joinColumns = @JoinColumn(name = "TaskId"),
            inverseJoinColumns = @JoinColumn(name = "HabitId")
    )
    private Set<Habit> habits;

    @ManyToMany
    @JoinTable(
            name = "NoteTasks", // Reutilizamos NoteTasks
            joinColumns = @JoinColumn(name = "TaskId"),
            inverseJoinColumns = @JoinColumn(name = "NoteId")
    )
    private Set<Note> notes;
}