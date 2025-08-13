package com.life.master_api.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
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

    // Campos para seguimiento diario
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingType trackingType = TrackingType.BOOLEAN;

    @Column
    private Integer dailyGoal;

    @Column(length = 50)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 7)
    private String color = "#007bff";

    @Column
    private LocalDate lastTrackedDate;

    @Column(nullable = false)
    private Boolean todayCompleted = false;

    @Column
    private Integer todayQuantity = 0;

    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Column(nullable = false)
    private Integer bestStreak = 0;

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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HabitHistory> history;

    // Constructor por defecto
    public Habit() {
        this.creation = new Date();
        this.trackingType = TrackingType.BOOLEAN;
        this.active = true;
        this.color = "#007bff";
        this.todayCompleted = false;
        this.todayQuantity = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
    }
}