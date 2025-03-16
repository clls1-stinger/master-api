package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Habits")
@Data
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany
    @JoinTable(
            name = "HabitCategories",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "CategoryId")
    )
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "HabitNotes",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "NoteId")
    )
    private Set<Note> notes;

    @ManyToMany
    @JoinTable(
            name = "HabitTasks",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "TaskId")
    )
    private Set<Task> tasks;
}