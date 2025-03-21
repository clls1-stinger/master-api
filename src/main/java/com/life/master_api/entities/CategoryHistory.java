package com.life.master_api.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "CategoriesHistory")
@Data
public class CategoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    private Long categoryId; // Referencia al ID de la categoría principal

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTimestamp; // Timestamp de la modificación que creó esta entrada de historial

}