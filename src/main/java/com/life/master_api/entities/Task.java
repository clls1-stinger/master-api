package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;  // Import

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Task")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only explicitly included
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Include // Include ID
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
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "HabitTasks", // Reutilizamos HabitTasks
            joinColumns = @JoinColumn(name = "TaskId"),
            inverseJoinColumns = @JoinColumn(name = "HabitId")
    )
    private Set<Habit> habits = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "NoteTasks", // Reutilizamos NoteTasks
            joinColumns = @JoinColumn(name = "TaskId"),
            inverseJoinColumns = @JoinColumn(name = "NoteId")
    )
    private Set<Note> notes = new HashSet<>();
}