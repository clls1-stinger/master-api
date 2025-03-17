package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // Import EqualsAndHashCode

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Categories")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Use only explicitly included fields
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Include  // Explicitly include the ID
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany(mappedBy = "categories")
    private Set<Task> tasks = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    private Set<Note> notes = new HashSet<>();

    @ManyToMany(mappedBy = "categories")
    private Set<Habit> habits = new HashSet<>();
}