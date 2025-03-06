package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Categories") // <-- Importante: Nombre de la tabla en la DB
@Data // <-- Lombok para getters, setters, toString, equals, hashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <-- Auto-incremento
    private Long id; // Usamos Long para bigint de PostgreSQL

    @Column(nullable = false) // <-- NOT NULL en la DB
    private String name;

    @Column(nullable = false) // <-- NOT NULL en la DB
    private String description;

    @Column(nullable = false) // <-- NOT NULL en la DB
    @Temporal(TemporalType.TIMESTAMP) // <-- Para mapear a tipo DATE en la DB
    private Date creation;
}