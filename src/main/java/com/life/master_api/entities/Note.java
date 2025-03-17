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

@Entity
@Table(name = "Notes")
@Data
@EqualsAndHashCode(of = "id")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note;

    @NotNull(message = "La fecha de creación no puede ser nula")
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

    @JsonIgnore
    @ManyToMany(mappedBy = "notes")
    private Set<Habit> habits = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "NoteTasks",
            joinColumns = @JoinColumn(name = "NoteId"),
            inverseJoinColumns = @JoinColumn(name = "TaskId")
    )
    private Set<Task> tasks = new HashSet<>();
}