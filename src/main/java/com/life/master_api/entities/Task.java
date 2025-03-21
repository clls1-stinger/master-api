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
@Table(name = "Task")
@Data
@EqualsAndHashCode(of = "id")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany
    @JoinTable(
            name = "TaskCategories",  // Match the exact table name from your database
            joinColumns = @JoinColumn(name = "TaskId"),  // Match the exact column name
            inverseJoinColumns = @JoinColumn(name = "CategoryId")  // Match the exact column name
    )
    private Set<Category> categories = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "tasks")
    private Set<Habit> habits = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "tasks")
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TaskHistory> history;
}