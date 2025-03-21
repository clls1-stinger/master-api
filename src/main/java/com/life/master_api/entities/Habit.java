package com.life.master_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // Import

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Habits")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Only explicitly included fields
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Include  // Include ID
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
    private Set<Category> categories = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "habits")
    private Set<Note> notes = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "habits")
    private Set<Task> tasks = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "habitId", cascade = CascadeType.ALL, orphanRemoval = true) // Nueva relación con HabitHistory
    private Set<HabitHistory> history = new HashSet<>(); // Campo para el historial
}