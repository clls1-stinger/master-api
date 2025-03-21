package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // Import

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Notes")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // Only explicitly included
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Include // Include ID
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
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "HabitNotes", // Reutilizamos HabitNotes
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "HabitId")
    )
    private Set<Habit> habits = new HashSet<>();

<<<<<<< Updated upstream

    @ManyToMany
    @JoinTable(
            name = "NoteTasks",
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "TaskId")
    )
=======
    @JsonIgnore
    @ManyToMany(mappedBy = "notes")
>>>>>>> Stashed changes
    private Set<Task> tasks = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, orphanRemoval = true) // Nueva relación con NoteHistory
    private Set<NoteHistory> history = new HashSet<>(); // Campo para el historial
}