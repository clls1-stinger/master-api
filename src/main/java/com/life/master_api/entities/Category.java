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
@Table(name = "Categories")
@Data
@EqualsAndHashCode(of = "id")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Task> tasks = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Note> notes = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "categories")
    private Set<Habit> habits = new HashSet<>();
}