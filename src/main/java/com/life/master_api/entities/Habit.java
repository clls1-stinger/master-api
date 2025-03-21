package com.life.master_api.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "Habits")
@Data
@EqualsAndHashCode(of = "id")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany
    @JoinTable(
            name = "HabitCategories",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "CategoryId")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "HabitNotes",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "NoteId")
    )
    private Set<Note> notes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "HabitTasks",
            joinColumns = @JoinColumn(name = "HabitId"),
            inverseJoinColumns = @JoinColumn(name = "TaskId")
    )
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HabitHistory> history;
}