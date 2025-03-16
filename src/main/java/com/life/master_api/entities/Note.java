package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Notes")
@Data
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany
    @JoinTable(
            name = "NoteCategories",
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "CategoryId")
    )
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "HabitNotes", // Reutilizamos HabitNotes
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "HabitId")
    )
    private Set<Habit> habits;

    @ManyToMany
    @JoinTable(
            name = "NoteTasks",
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "TaskId")
    )
    private Set<Task> tasks;
}