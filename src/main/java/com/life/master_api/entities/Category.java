package com.life.master_api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @ManyToMany(mappedBy = "categories")
    private Set<Task> tasks;

    @ManyToMany(mappedBy = "categories")
    private Set<Note> notes;

    @ManyToMany(mappedBy = "categories")
    private Set<Habit> habits;
}